package com.example.alumnisearch.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "phantombuster")
public class PhantomBusterConfig {

    /**
     * PhantomBuster API Key (X-Phantombuster-Key header)
     */
    private String apiKey = "";

    /**
     * Agent ID of the LinkedIn Search Export Phantom
     */
    private String agentId = "";

    /**
     * Base URL for PhantomBuster v2 API
     */
    private String baseUrl = "https://api.phantombuster.com/api/v2";

    /**
     * If true or if API key is not provided, mock client is used for responses.
     */
    private boolean mockMode = false;

    /**
     * Polling interval in milliseconds when waiting for scrape completion
     */
    private long pollIntervalMs = 2000;

    /**
     * Maximum time in seconds to wait for phantom execution to finish
     */
    private int maxWaitSeconds = 30;

    public boolean isConfigured() {
        return apiKey != null && !apiKey.trim().isEmpty() && !mockMode;
    }
}
