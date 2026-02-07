package com.iagro.pettersson.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Se hardcodea en dev, se deben usar variables de entorno
    @Value("${gemini.api.url}")
    private String baseUrl;
    @Value("${gemini.api.key}")
    private String apiKey;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("X-goog-api-key", apiKey)
                .build();
    }
}
