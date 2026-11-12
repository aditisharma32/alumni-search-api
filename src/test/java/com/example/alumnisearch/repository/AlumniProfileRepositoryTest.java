package com.example.alumnisearch.repository;

import com.example.alumnisearch.entity.AlumniProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class AlumniProfileRepositoryTest {

    @Autowired
    private AlumniProfileRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Should save and retrieve alumni profile by university")
    void testSaveAndFindByUniversity() {
        AlumniProfile profile = AlumniProfile.builder()
                .name("John Doe")
                .currentRole("Software Engineer")
                .university("University of XYZ")
                .location("New York, NY")
                .linkedinHeadline("Passionate Software Engineer at XYZ Corp")
                .passoutYear(2020)
                .build();

        repository.save(profile);

        List<AlumniProfile> found = repository.findByUniversityIgnoreCase("university of xyz");
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("John Doe");
        assertThat(found.get(0).getCurrentRole()).isEqualTo("Software Engineer");
    }

    @Test
    @DisplayName("Should find alumni profile by name and university ignore case")
    void testFindByNameAndUniversity() {
        AlumniProfile profile = AlumniProfile.builder()
                .name("Jane Smith")
                .currentRole("Data Scientist")
                .university("University of XYZ")
                .location("San Francisco, CA")
                .linkedinHeadline("Data Scientist | AI Enthusiast")
                .passoutYear(2019)
                .build();

        repository.save(profile);

        Optional<AlumniProfile> found = repository.findByNameIgnoreCaseAndUniversityIgnoreCase("jane smith", "UNIVERSITY OF XYZ");
        assertThat(found).isPresent();
        assertThat(found.get().getPassoutYear()).isEqualTo(2019);
    }
}
