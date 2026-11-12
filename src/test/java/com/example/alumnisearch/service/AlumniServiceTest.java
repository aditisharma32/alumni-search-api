package com.example.alumnisearch.service;

import com.example.alumnisearch.client.PhantomBusterClient;
import com.example.alumnisearch.dto.AlumniResponseDto;
import com.example.alumnisearch.dto.AlumniSearchRequest;
import com.example.alumnisearch.entity.AlumniProfile;
import com.example.alumnisearch.repository.AlumniProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlumniServiceTest {

    @Mock
    private PhantomBusterClient phantomBusterClient;

    @Mock
    private AlumniProfileRepository alumniProfileRepository;

    @InjectMocks
    private AlumniServiceImpl alumniService;

    private AlumniSearchRequest searchRequest;
    private AlumniResponseDto sampleDto;

    @BeforeEach
    void setUp() {
        searchRequest = AlumniSearchRequest.builder()
                .university("University of XYZ")
                .designation("Software Engineer")
                .passoutYear(2020)
                .build();

        sampleDto = AlumniResponseDto.builder()
                .name("John Doe")
                .currentRole("Software Engineer")
                .university("University of XYZ")
                .location("New York, NY")
                .linkedinHeadline("Passionate Software Engineer at XYZ Corp")
                .passoutYear(2020)
                .build();
    }

    @Test
    @DisplayName("Should fetch from PhantomBuster, persist to DB, and return DTOs")
    void testSearchAndSaveAlumni_Success() {
        when(phantomBusterClient.searchAlumni("University of XYZ", "Software Engineer", 2020))
                .thenReturn(List.of(sampleDto));

        when(alumniProfileRepository.findByNameIgnoreCaseAndUniversityIgnoreCase(anyString(), anyString()))
                .thenReturn(Optional.empty());

        when(alumniProfileRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AlumniResponseDto> result = alumniService.searchAndSaveAlumni(searchRequest);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("John Doe");
        assertThat(result.get(0).getCurrentRole()).isEqualTo("Software Engineer");
        assertThat(result.get(0).getUniversity()).isEqualTo("University of XYZ");

        verify(phantomBusterClient, times(1)).searchAlumni("University of XYZ", "Software Engineer", 2020);
        verify(alumniProfileRepository, times(1)).saveAll(any());
    }

    @Test
    @DisplayName("Should update existing alumni profile when duplicate is found")
    void testSearchAndSaveAlumni_UpdateExisting() {
        AlumniProfile existingProfile = AlumniProfile.builder()
                .id(1L)
                .name("John Doe")
                .university("University of XYZ")
                .currentRole("Junior Developer")
                .location("Old Location")
                .passoutYear(2020)
                .build();

        when(phantomBusterClient.searchAlumni("University of XYZ", "Software Engineer", 2020))
                .thenReturn(List.of(sampleDto));

        when(alumniProfileRepository.findByNameIgnoreCaseAndUniversityIgnoreCase("John Doe", "University of XYZ"))
                .thenReturn(Optional.of(existingProfile));

        when(alumniProfileRepository.saveAll(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        List<AlumniResponseDto> result = alumniService.searchAndSaveAlumni(searchRequest);

        assertThat(result).hasSize(1);
        assertThat(existingProfile.getCurrentRole()).isEqualTo("Software Engineer");
        assertThat(existingProfile.getLocation()).isEqualTo("New York, NY");
    }

    @Test
    @DisplayName("Should retrieve all saved alumni profiles from database")
    void testGetAllAlumni() {
        AlumniProfile profile = AlumniProfile.builder()
                .name("Jane Smith")
                .currentRole("Data Scientist")
                .university("University of XYZ")
                .location("San Francisco, CA")
                .linkedinHeadline("Data Scientist | AI Enthusiast")
                .passoutYear(2019)
                .build();

        when(alumniProfileRepository.findAll()).thenReturn(List.of(profile));

        List<AlumniResponseDto> result = alumniService.getAllAlumni();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Smith");
        assertThat(result.get(0).getCurrentRole()).isEqualTo("Data Scientist");
    }
}
