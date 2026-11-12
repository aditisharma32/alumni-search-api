package com.example.alumnisearch.controller;

import com.example.alumnisearch.dto.AlumniResponseDto;
import com.example.alumnisearch.dto.AlumniSearchRequest;
import com.example.alumnisearch.exception.PhantomBusterApiException;
import com.example.alumnisearch.service.AlumniService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlumniController.class)
class AlumniControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlumniService alumniService;

    @Test
    @DisplayName("POST /api/alumni/search - returns 200 and expected response structure")
    void testSearchAlumni_Success() throws Exception {
        AlumniSearchRequest request = AlumniSearchRequest.builder()
                .university("University of XYZ")
                .designation("Software Engineer")
                .passoutYear(2020)
                .build();

        AlumniResponseDto dto1 = AlumniResponseDto.builder()
                .name("John Doe")
                .currentRole("Software Engineer")
                .university("University of XYZ")
                .location("New York, NY")
                .linkedinHeadline("Passionate Software Engineer at XYZ Corp")
                .passoutYear(2020)
                .build();

        AlumniResponseDto dto2 = AlumniResponseDto.builder()
                .name("Jane Smith")
                .currentRole("Data Scientist")
                .university("University of XYZ")
                .location("San Francisco, CA")
                .linkedinHeadline("Data Scientist | AI Enthusiast")
                .passoutYear(2019)
                .build();

        when(alumniService.searchAndSaveAlumni(any(AlumniSearchRequest.class)))
                .thenReturn(List.of(dto1, dto2));

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].name", is("John Doe")))
                .andExpect(jsonPath("$.data[0].currentRole", is("Software Engineer")))
                .andExpect(jsonPath("$.data[0].university", is("University of XYZ")))
                .andExpect(jsonPath("$.data[0].location", is("New York, NY")))
                .andExpect(jsonPath("$.data[0].linkedinHeadline", is("Passionate Software Engineer at XYZ Corp")))
                .andExpect(jsonPath("$.data[0].passoutYear", is(2020)))
                .andExpect(jsonPath("$.data[1].name", is("Jane Smith")));
    }

    @Test
    @DisplayName("POST /api/alumni/search - returns 400 when university is missing")
    void testSearchAlumni_MissingUniversity_Returns400() throws Exception {
        String invalidPayload = """
                {
                    "university": "",
                    "designation": "Software Engineer"
                }
                """;

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("Validation failed")))
                .andExpect(jsonPath("$.errors", hasItem(containsString("university"))));
    }

    @Test
    @DisplayName("POST /api/alumni/search - returns 400 when designation is missing")
    void testSearchAlumni_MissingDesignation_Returns400() throws Exception {
        String invalidPayload = """
                {
                    "university": "Stanford University",
                    "designation": ""
                }
                """;

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.errors", hasItem(containsString("designation"))));
    }

    @Test
    @DisplayName("POST /api/alumni/search - returns 502 Bad Gateway on PhantomBuster API error")
    void testSearchAlumni_PhantomBusterError_Returns502() throws Exception {
        AlumniSearchRequest request = AlumniSearchRequest.builder()
                .university("University of XYZ")
                .designation("Software Engineer")
                .build();

        when(alumniService.searchAndSaveAlumni(any(AlumniSearchRequest.class)))
                .thenThrow(new PhantomBusterApiException("PhantomBuster rate limit exceeded"));

        mockMvc.perform(post("/api/alumni/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status", is("error")))
                .andExpect(jsonPath("$.message", containsString("PhantomBuster error")));
    }

    @Test
    @DisplayName("GET /api/alumni/all - returns 200 and list of saved profiles")
    void testGetAllAlumni_Success() throws Exception {
        AlumniResponseDto dto = AlumniResponseDto.builder()
                .name("John Doe")
                .currentRole("Software Engineer")
                .university("University of XYZ")
                .location("New York, NY")
                .linkedinHeadline("Passionate Software Engineer at XYZ Corp")
                .passoutYear(2020)
                .build();

        when(alumniService.getAllAlumni()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/alumni/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("success")))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("John Doe")));
    }
}
