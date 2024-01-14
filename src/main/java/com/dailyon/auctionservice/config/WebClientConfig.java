package com.dailyon.auctionservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    private final Environment env;

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(env.getProperty("endpoint.product-service"))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 *1024))
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }
}
