package com.example.alumnisearch.client;

import com.example.alumnisearch.dto.AlumniResponseDto;

import java.util.List;

/**
 * Strategy/Adapter interface for external LinkedIn scraping via PhantomBuster API.
 */
public interface PhantomBusterClient {

    /**
     * Executes LinkedIn search on PhantomBuster for alumni matching parameters.
     *
     * @param university Educational institution name
     * @param designation Current job title / role
     * @param passoutYear Optional graduation year
     * @return List of retrieved alumni profile DTOs
     */
    List<AlumniResponseDto> searchAlumni(String university, String designation, Integer passoutYear);
}
