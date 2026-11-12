package com.example.alumnisearch.repository;

import com.example.alumnisearch.entity.AlumniProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumniProfileRepository extends JpaRepository<AlumniProfile, Long> {

    List<AlumniProfile> findByUniversityIgnoreCase(String university);

    List<AlumniProfile> findByUniversityIgnoreCaseAndCurrentRoleIgnoreCase(String university, String currentRole);

    Optional<AlumniProfile> findByNameIgnoreCaseAndUniversityIgnoreCase(String name, String university);

    Optional<AlumniProfile> findByLinkedinUrl(String linkedinUrl);

    boolean existsByNameIgnoreCaseAndUniversityIgnoreCase(String name, String university);
}
