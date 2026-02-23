package com.medilabo.ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestConfig is a Spring configuration class that defines a RestTemplate bean for making RESTful web service calls.
 * The RestTemplate is a synchronous client to perform HTTP requests, exposing a simple template method API over underlying HTTP client libraries.
 */
@Configuration
public class RestConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
