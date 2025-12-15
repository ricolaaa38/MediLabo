package com.medilabo.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Configuration
public class GatewayConfig {

    @Value("${internal.secret}")
    private String internalSecret;

    @Bean
    public GlobalFilter addInternalSecretHeaderFilter() {
        return (exchange, chain) -> exchange.getPrincipal()
                .cast(org.springframework.security.core.Authentication.class)
                .flatMap(authentication -> {
                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                            .header("X-Internal-Secret", internalSecret);
                    if (authentication != null && authentication.isAuthenticated()) {
                        String role = authentication.getAuthorities().stream()
                                .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                                .findFirst()
                                .orElse("ROLE_ANONYMOUS");
                        requestBuilder.header("X-User-Role", role);
                    }
                    return chain.filter(exchange.mutate().request(requestBuilder.build()).build());
                })
                .switchIfEmpty(Mono.empty());
    }
}
