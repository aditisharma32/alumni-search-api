package com.example.alumnisearch.client;

import com.example.alumnisearch.dto.AlumniResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock/Sandbox implementation of PhantomBusterClient.
 * Simulates scraping LinkedIn alumni profiles when running locally, in test suites,
 * or when PhantomBuster credentials are not yet configured.
 */
@Component("mockPhantomBusterClient")
public class MockPhantomBusterClient implements PhantomBusterClient {

    private static final Logger log = LoggerFactory.getLogger(MockPhantomBusterClient.class);

    @Override
    public List<AlumniResponseDto> searchAlumni(String university, String designation, Integer passoutYear) {
        log.info("Executing Mock PhantomBuster LinkedIn Search for University: '{}', Designation: '{}', Year: {}",
                university, designation, passoutYear);

        int baseYear = (passoutYear != null && passoutYear > 1950) ? passoutYear : 2020;

        List<AlumniResponseDto> mockAlumni = new ArrayList<>();

        mockAlumni.add(AlumniResponseDto.builder()
                .name("John Doe")
                .currentRole(designation)
                .university(university)
                .location("New York, NY")
                .linkedinHeadline("Passionate " + designation + " | " + university + " Alum")
                .passoutYear(baseYear)
                .build());

        mockAlumni.add(AlumniResponseDto.builder()
                .name("Jane Smith")
                .currentRole(designation.equalsIgnoreCase("Software Engineer") ? "Senior Software Engineer" : designation)
                .university(university)
                .location("San Francisco, CA")
                .linkedinHeadline(designation + " | Tech Innovator & " + university + " Alumna")
                .passoutYear(baseYear > 1950 ? baseYear - 1 : 2019)
                .build());

        mockAlumni.add(AlumniResponseDto.builder()
                .name("Alex Johnson")
                .currentRole(designation)
                .university(university)
                .location("Seattle, WA")
                .linkedinHeadline("Building high-scale distributed systems | " + designation)
                .passoutYear(baseYear > 1950 ? baseYear : 2021)
                .build());

        mockAlumni.add(AlumniResponseDto.builder()
                .name("Priya Sharma")
                .currentRole("Lead " + designation)
                .university(university)
                .location("Austin, TX")
                .linkedinHeadline("Engineering Leader | Proud " + university + " Grad (" + baseYear + ")")
                .passoutYear(baseYear > 1950 ? baseYear + 1 : 2022)
                .build());

        return mockAlumni;
    }
}
