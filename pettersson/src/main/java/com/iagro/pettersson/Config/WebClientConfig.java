package com.iagro.pettersson.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    // Se hardcodea en dev, se deben usar variables de entorno
    private final String BASEURL = "https://generativelanguage.googleapis.com";
    private final String APIKEY = "AIzaSyBO1zeO2A0YXcQ_DIqau3omkO6sHAmZQqk";

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(BASEURL)
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("X-goog-api-key", APIKEY)
                .build();
    }
}
