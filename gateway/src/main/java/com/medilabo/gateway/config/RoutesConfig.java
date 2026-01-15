package com.medilabo.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // / -> /ui
                .route("ui-root", r -> r
                        .path("/")
                        .filters(f -> f.setPath("/ui"))
                        .uri("http://localhost:8082"))
                // /ui/**
                .route("ui", r -> r
                        .path("/ui/**")
//                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8082"))
                .route("ui-static", r -> r
                        .path("/css/**", "/js/**", "/images/**", "/webjars/**")
                        .uri("http://localhost:8082"))
                // /api/patients/**
                .route("patient", r -> r
                        .path("/api/patients/**")
//                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8080"))
                // /api/notes/**
                .route("notes", r -> r
                        .path("/api/notes/**")
//                        .filters(f -> f.stripPrefix(1))
                        .uri("http://localhost:8083"))
                .build();
    }
}
