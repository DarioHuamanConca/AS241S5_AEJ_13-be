package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Value("${rapidapi.key}")
    private String rapidApiKey;

    @Value("${rapidapi.llama.url}")
    private String llamaUrl;

    @Value("${rapidapi.llama.host}")
    private String llamaHost;

    @Value("${rapidapi.tts.url}")
    private String ttsUrl;

    @Value("${rapidapi.tts.host}")
    private String ttsHost;

    @Bean
    public WebClient llamaWebClient() {
        return WebClient.builder()
                .baseUrl(llamaUrl)
                .defaultHeader("x-rapidapi-key", rapidApiKey)
                .defaultHeader("x-rapidapi-host", llamaHost)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    @Bean
    public WebClient ttsWebClient() {
        return WebClient.builder()
                .baseUrl(ttsUrl)
                .defaultHeader("x-rapidapi-key", rapidApiKey)
                .defaultHeader("x-rapidapi-host", ttsHost)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}
