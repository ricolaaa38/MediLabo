package com.medilabo.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Value("${ui.url}")
    private String uiUrl;

    @Value("${patient.url}")
    private String patientUrl;

    @Value("${note.url}")
    private String notesUrl;

    @Value("${evaluation.url}")
    private String evaluationUrl;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ui-root", r -> r
                        .path("/")
                        .filters(f -> f.setPath("/ui"))
                        .uri(uiUrl))
                .route("ui", r -> r
                        .path("/ui/**")
                        .uri(uiUrl))
                .route("ui-static", r -> r
                        .path("/css/**", "/js/**", "/images/**", "/webjars/**")
                        .uri(uiUrl))
                .route("patient", r -> r
                        .path("/api/patients/**")
                        .uri(patientUrl))
                .route("notes", r -> r
                        .path("/api/notes/**")
                        .uri(notesUrl))
                .route("evaluation", r -> r
                        .path("/api/evaluations/**")
                        .uri(evaluationUrl))
                .build();
    }
}
