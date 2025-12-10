package com.medilabo.gateway.config;

import com.medilabo.gateway.model.User;
import com.medilabo.gateway.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initialisation(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "user@example.com";
            String password = "password";
            userRepository.existsByEmail(email)
                .flatMap(exists -> exists ? Mono.empty() : userRepository.save(new User(null, email, passwordEncoder.encode(password))))

                .subscribe();
        };
    }
}
