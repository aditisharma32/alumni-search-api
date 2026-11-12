package com.example.alumnisearch.service;

import com.example.alumnisearch.client.PhantomBusterClient;
import com.example.alumnisearch.dto.AlumniResponseDto;
import com.example.alumnisearch.dto.AlumniSearchRequest;
import com.example.alumnisearch.entity.AlumniProfile;
import com.example.alumnisearch.repository.AlumniProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AlumniServiceImpl implements AlumniService {

    private static final Logger log = LoggerFactory.getLogger(AlumniServiceImpl.class);

    private final PhantomBusterClient phantomBusterClient;
    private final AlumniProfileRepository alumniProfileRepository;

    public AlumniServiceImpl(PhantomBusterClient phantomBusterClient, AlumniProfileRepository alumniProfileRepository) {
        this.phantomBusterClient = phantomBusterClient;
        this.alumniProfileRepository = alumniProfileRepository;
    }

    @Override
    @Transactional
    public List<AlumniResponseDto> searchAndSaveAlumni(AlumniSearchRequest request) {
        String university = request.getUniversity().trim();
        String designation = request.getDesignation().trim();
        Integer passoutYear = request.getPassoutYear();

        log.info("Starting alumni search & persist workflow for University='{}', Designation='{}', Year={}",
                university, designation, passoutYear);

        // 1. Fetch scraped profiles from PhantomBuster
        List<AlumniResponseDto> scrapedProfiles = phantomBusterClient.searchAlumni(university, designation, passoutYear);

        if (scrapedProfiles == null || scrapedProfiles.isEmpty()) {
            log.info("No alumni profiles retrieved from PhantomBuster for query: {} - {}", university, designation);
            return List.of();
        }

        // 2. Persist & deduplicate into Database
        List<AlumniProfile> entitiesToSave = new ArrayList<>();
        for (AlumniResponseDto dto : scrapedProfiles) {
            if (dto.getName() == null || dto.getName().isBlank()) {
                continue;
            }

            Optional<AlumniProfile> existingOpt = alumniProfileRepository
                    .findByNameIgnoreCaseAndUniversityIgnoreCase(dto.getName().trim(), dto.getUniversity().trim());

            AlumniProfile profile;
            if (existingOpt.isPresent()) {
                profile = existingOpt.get();
                profile.setCurrentRole(dto.getCurrentRole());
                profile.setLocation(dto.getLocation());
                profile.setLinkedinHeadline(dto.getLinkedinHeadline());
                if (dto.getPassoutYear() != null) {
                    profile.setPassoutYear(dto.getPassoutYear());
                }
            } else {
                profile = AlumniProfile.builder()
                        .name(dto.getName().trim())
                        .currentRole(dto.getCurrentRole())
                        .university(dto.getUniversity().trim())
                        .location(dto.getLocation())
                        .linkedinHeadline(dto.getLinkedinHeadline())
                        .passoutYear(dto.getPassoutYear())
                        .build();
            }
            entitiesToSave.add(profile);
        }

        List<AlumniProfile> savedEntities = alumniProfileRepository.saveAll(entitiesToSave);
        log.info("Successfully persisted {} alumni profiles to database", savedEntities.size());

        // 3. Return mapped response DTOs
        return savedEntities.stream()
                .map(AlumniResponseDto::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlumniResponseDto> getAllAlumni() {
        log.info("Retrieving all saved alumni profiles from database");
        return alumniProfileRepository.findAll().stream()
                .map(AlumniResponseDto::fromEntity)
                .toList();
    }
}
