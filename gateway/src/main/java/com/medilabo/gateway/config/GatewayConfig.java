// java
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
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest.Builder requestBuilder = request.mutate()
                    .header("X-Internal-Secret", internalSecret);

            return exchange.getPrincipal()
                    .cast(org.springframework.security.core.Authentication.class)
                    .switchIfEmpty(Mono.empty())
                    .map(authentication -> {
                        if (authentication != null && authentication.isAuthenticated()) {
                            String role = authentication.getAuthorities().stream()
                                    .map(org.springframework.security.core.GrantedAuthority::getAuthority)
                                    .findFirst()
                                    .orElse("ROLE_ANONYMOUS");
                            requestBuilder.header("X-User-Role", role);
                        }       return exchange.mutate().request(requestBuilder.build()).build();
                    })
                    .defaultIfEmpty(exchange.mutate().request(requestBuilder.build()).build())
                    .flatMap(e -> {
                        // log à cet endroit
                        System.out.println("GW -> path=" + e.getRequest().getURI()
                                + " X-Internal-Secret=" + e.getRequest().getHeaders().getFirst("X-Internal-Secret")
                                + " X-User-Role=" + e.getRequest().getHeaders().getFirst("X-User-Role"));
                        return chain.filter(e);
                    });
        };
    }
}
