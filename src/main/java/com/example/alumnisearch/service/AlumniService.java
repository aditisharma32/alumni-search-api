package com.example.alumnisearch.service;

import com.example.alumnisearch.dto.AlumniResponseDto;
import com.example.alumnisearch.dto.AlumniSearchRequest;

import java.util.List;

public interface AlumniService {

    /**
     * Searches LinkedIn alumni profiles via PhantomBuster API, saves them into the database,
     * and returns the list of alumni.
     *
     * @param request Search parameters containing university, designation, and optional passout year
     * @return List of retrieved and persisted alumni profiles
     */
    List<AlumniResponseDto> searchAndSaveAlumni(AlumniSearchRequest request);

    /**
     * Retrieves all saved alumni profiles stored in the database.
     *
     * @return List of all saved alumni profiles
     */
    List<AlumniResponseDto> getAllAlumni();
}
