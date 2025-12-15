package com.medilabo.gateway.config;

import com.medilabo.gateway.model.Role;
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
            String email1 = "docteur@example.com";
            String password1 = "password";
            userRepository.existsByEmail(email1)
                .flatMap(exists -> exists ? Mono.empty() : userRepository.save(new User(null, email1, passwordEncoder.encode(password1), Role.PRATICIEN)))
                .subscribe();

            String email2 = "organisateur@example.com";
            String password2 = "password";
            userRepository.existsByEmail(email2)
                .flatMap(exists -> exists ? Mono.empty() : userRepository.save(new User(null, email2, passwordEncoder.encode(password2), Role.ORGANISATEUR)))
                .subscribe();
        };
    }
}
