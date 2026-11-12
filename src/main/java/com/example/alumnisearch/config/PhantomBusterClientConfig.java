package com.example.alumnisearch.config;

import com.example.alumnisearch.client.PhantomBusterClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Strategy/Factory configuration to dynamically select between Live and Mock
 * PhantomBuster clients based on application configuration and environment.
 */
@Configuration
public class PhantomBusterClientConfig {

    private static final Logger log = LoggerFactory.getLogger(PhantomBusterClientConfig.class);

    @Bean
    @Primary
    public PhantomBusterClient phantomBusterClient(
            PhantomBusterConfig config,
            @Qualifier("realPhantomBusterClient") PhantomBusterClient realClient,
            @Qualifier("mockPhantomBusterClient") PhantomBusterClient mockClient) {

        if (config.isConfigured()) {
            log.info("PhantomBuster API Key detected. Using Live PhantomBusterClient.");
            return realClient;
        } else {
            log.info("PhantomBuster API Key is not set or mock-mode is active. Using MockPhantomBusterClient.");
            return mockClient;
        }
    }
}
