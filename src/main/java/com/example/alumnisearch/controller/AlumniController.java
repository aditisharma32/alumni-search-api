package com.example.alumnisearch.controller;

import com.example.alumnisearch.dto.AlumniResponseDto;
import com.example.alumnisearch.dto.AlumniSearchRequest;
import com.example.alumnisearch.dto.ApiResponse;
import com.example.alumnisearch.service.AlumniService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
@Tag(name = "Alumni Management", description = "Endpoints for searching LinkedIn alumni profiles using PhantomBuster and retrieving persisted profiles")
public class AlumniController {

    private static final Logger log = LoggerFactory.getLogger(AlumniController.class);

    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    /**
     * Search LinkedIn alumni profiles using PhantomBuster and persist retrieved results.
     *
     * POST /api/alumni/search
     */
    @PostMapping("/search")
    @Operation(summary = "Search & Save Alumni Profiles", description = "Queries PhantomBuster to scrape LinkedIn alumni for the given university and designation, persists them in the database, and returns the alumni list.")
    public ResponseEntity<ApiResponse<List<AlumniResponseDto>>> searchAlumni(
            @Valid @RequestBody AlumniSearchRequest request) {
        log.info("Received alumni search request: university='{}', designation='{}', year={}",
                request.getUniversity(), request.getDesignation(), request.getPassoutYear());

        List<AlumniResponseDto> results = alumniService.searchAndSaveAlumni(request);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * Retrieve all saved alumni profiles from the database.
     *
     * GET /api/alumni/all
     */
    @GetMapping("/all")
    @Operation(summary = "Get All Saved Alumni", description = "Retrieves all saved alumni profiles currently persisted in the database.")
    public ResponseEntity<ApiResponse<List<AlumniResponseDto>>> getAllAlumni() {
        log.info("Received request to fetch all saved alumni profiles");
        List<AlumniResponseDto> results = alumniService.getAllAlumni();
        return ResponseEntity.ok(ApiResponse.success(results));
    }
}
