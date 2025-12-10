package com.medilabo.gateway.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Configuration
public class GatewayConfig {

    @Value("${internal.secret}")
    private String internalSecret;

    @Bean
    public GlobalFilter addInternalSecretHeaderFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest mutated = exchange.getRequest().mutate()
                .header("X-Internal-Secret", internalSecret)
                .build();
            return chain.filter(exchange.mutate().request(mutated).build());
        };
    }
}
