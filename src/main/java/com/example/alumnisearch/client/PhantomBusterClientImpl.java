package com.example.alumnisearch.client;

import com.example.alumnisearch.config.PhantomBusterConfig;
import com.example.alumnisearch.dto.AlumniResponseDto;
import com.example.alumnisearch.exception.PhantomBusterApiException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;

/**
 * Production implementation of PhantomBusterClient that connects directly to
 * PhantomBuster's v2 REST API.
 */
@Component("realPhantomBusterClient")
public class PhantomBusterClientImpl implements PhantomBusterClient {

    private static final Logger log = LoggerFactory.getLogger(PhantomBusterClientImpl.class);

    private final PhantomBusterConfig config;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public PhantomBusterClientImpl(PhantomBusterConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .defaultHeader("X-Phantombuster-Key", config.getApiKey())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<AlumniResponseDto> searchAlumni(String university, String designation, Integer passoutYear) {
        if (!config.isConfigured()) {
            throw new PhantomBusterApiException("PhantomBuster API Key is not configured. Set 'phantombuster.api-key' or enable mock mode.");
        }

        String agentId = config.getAgentId();
        if (agentId == null || agentId.isBlank()) {
            throw new PhantomBusterApiException("PhantomBuster Agent ID is not configured. Set 'phantombuster.agent-id'.");
        }

        log.info("Launching PhantomBuster Agent {} for query: university='{}', role='{}', year={}",
                agentId, university, designation, passoutYear);

        // 1. Build launch arguments
        Map<String, Object> argument = new HashMap<>();
        String query = buildLinkedInSearchQuery(university, designation, passoutYear);
        argument.put("search", query);
        argument.put("university", university);
        argument.put("designation", designation);
        if (passoutYear != null) {
            argument.put("passoutYear", passoutYear);
        }

        Map<String, Object> launchPayload = new HashMap<>();
        launchPayload.put("output", "json");
        launchPayload.put("argument", argument);

        String containerId;
        try {
            JsonNode launchResponse = restClient.post()
                    .uri("/agents/{agentId}/launch", agentId)
                    .body(launchPayload)
                    .retrieve()
                    .body(JsonNode.class);

            if (launchResponse == null || !launchResponse.has("data")) {
                throw new PhantomBusterApiException("Failed to launch PhantomBuster agent: Empty response");
            }
            containerId = launchResponse.path("data").path("containerId").asText(null);
            log.info("Agent launched successfully with containerId: {}", containerId);
        } catch (RestClientResponseException ex) {
            log.error("PhantomBuster launch error: HTTP {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
            throw new PhantomBusterApiException("Failed to launch PhantomBuster agent: " + ex.getResponseBodyAsString(),
                    ex.getStatusCode().value());
        } catch (Exception ex) {
            log.error("Error communicating with PhantomBuster API", ex);
            throw new PhantomBusterApiException("Error communicating with PhantomBuster: " + ex.getMessage(), ex);
        }

        // 2. Poll for container / agent output
        return waitForAndParseOutput(containerId, agentId, university, designation, passoutYear);
    }

    private String buildLinkedInSearchQuery(String university, String designation, Integer passoutYear) {
        StringBuilder query = new StringBuilder();
        query.append("\"").append(university).append("\" ");
        query.append("\"").append(designation).append("\"");
        if (passoutYear != null) {
            query.append(" ").append(passoutYear);
        }
        return query.toString();
    }

    private List<AlumniResponseDto> waitForAndParseOutput(String containerId, String agentId,
                                                         String university, String designation, Integer passoutYear) {
        long startTime = System.currentTimeMillis();
        long maxWaitMs = config.getMaxWaitSeconds() * 1000L;

        while (System.currentTimeMillis() - startTime < maxWaitMs) {
            try {
                // Fetch container or agent output
                JsonNode outputNode;
                if (containerId != null && !containerId.isBlank()) {
                    outputNode = restClient.get()
                            .uri("/containers/fetch-output?id={id}", containerId)
                            .retrieve()
                            .body(JsonNode.class);
                } else {
                    outputNode = restClient.get()
                            .uri("/agents/fetch-output?id={id}", agentId)
                            .retrieve()
                            .body(JsonNode.class);
                }

                if (outputNode != null && outputNode.has("data")) {
                    JsonNode data = outputNode.get("data");
                    JsonNode rawOutput = data.has("output") ? data.get("output") : data.get("resultObject");

                    if (rawOutput != null && !rawOutput.isNull()) {
                        List<AlumniResponseDto> results = parsePhantomBusterJson(rawOutput, university, designation, passoutYear);
                        if (!results.isEmpty()) {
                            log.info("Successfully fetched {} alumni records from PhantomBuster", results.size());
                            return results;
                        }
                    }
                }

                Thread.sleep(config.getPollIntervalMs());
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new PhantomBusterApiException("PhantomBuster polling interrupted", ie);
            } catch (Exception ex) {
                log.warn("Transient error while checking PhantomBuster output: {}", ex.getMessage());
            }
        }

        log.warn("PhantomBuster execution timed out after {} seconds. Checking if agent output exists...", config.getMaxWaitSeconds());
        return Collections.emptyList();
    }

    public List<AlumniResponseDto> parsePhantomBusterJson(JsonNode outputNode, String defaultUniv, String defaultRole, Integer defaultYear) {
        List<AlumniResponseDto> list = new ArrayList<>();
        try {
            JsonNode arrayNode = outputNode;
            if (outputNode.isTextual()) {
                // Sometimes output is a serialized JSON string
                arrayNode = objectMapper.readTree(outputNode.asText());
            }

            if (arrayNode.isArray()) {
                for (JsonNode item : arrayNode) {
                    AlumniResponseDto dto = mapJsonToDto(item, defaultUniv, defaultRole, defaultYear);
                    if (dto != null && dto.getName() != null && !dto.getName().isBlank()) {
                        list.add(dto);
                    }
                }
            } else if (arrayNode.isObject()) {
                AlumniResponseDto dto = mapJsonToDto(arrayNode, defaultUniv, defaultRole, defaultYear);
                if (dto != null && dto.getName() != null && !dto.getName().isBlank()) {
                    list.add(dto);
                }
            }
        } catch (Exception ex) {
            log.error("Failed to parse PhantomBuster JSON output: {}", ex.getMessage());
        }
        return list;
    }

    private AlumniResponseDto mapJsonToDto(JsonNode node, String defaultUniv, String defaultRole, Integer defaultYear) {
        String name = extractField(node, "name", "fullName", "profileName", "general.fullName");
        String role = extractField(node, "currentRole", "role", "title", "jobTitle", "occupation", "company");
        String univ = extractField(node, "university", "school", "education", "college");
        String location = extractField(node, "location", "general.location", "city");
        String headline = extractField(node, "linkedinHeadline", "headline", "general.headline", "summary");
        Integer year = extractYear(node, "passoutYear", "graduationYear", "year", "degreeYear");

        if (name == null || name.isBlank()) {
            return null;
        }

        return AlumniResponseDto.builder()
                .name(name.trim())
                .currentRole(role != null && !role.isBlank() ? role.trim() : defaultRole)
                .university(univ != null && !univ.isBlank() ? univ.trim() : defaultUniv)
                .location(location != null ? location.trim() : "Unknown")
                .linkedinHeadline(headline != null ? headline.trim() : (role + " | " + univ))
                .passoutYear(year != null ? year : defaultYear)
                .build();
    }

    private String extractField(JsonNode node, String... candidatePaths) {
        for (String path : candidatePaths) {
            if (path.contains(".")) {
                String[] parts = path.split("\\.");
                JsonNode cur = node;
                for (String part : parts) {
                    if (cur != null) cur = cur.get(part);
                }
                if (cur != null && !cur.isNull() && !cur.asText().isBlank()) {
                    return cur.asText();
                }
            } else {
                JsonNode val = node.get(path);
                if (val != null && !val.isNull() && !val.asText().isBlank()) {
                    return val.asText();
                }
            }
        }
        return null;
    }

    private Integer extractYear(JsonNode node, String... candidatePaths) {
        for (String path : candidatePaths) {
            JsonNode val = node.get(path);
            if (val != null && !val.isNull()) {
                if (val.isNumber()) {
                    return val.asInt();
                }
                try {
                    return Integer.parseInt(val.asText().trim());
                } catch (NumberFormatException ignored) {}
            }
        }
        return null;
    }
}
