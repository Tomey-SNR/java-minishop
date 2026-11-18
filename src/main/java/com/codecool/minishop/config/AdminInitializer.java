package com.codecool.minishop.config;

import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    public CommandLineRunner createAdmin(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${ADMIN_USERNAME}") String username,
            @Value("${ADMIN_EMAIL}") String email,
            @Value("${ADMIN_PASSWORD}") String password) {

        return args -> {
            var existingUser = userRepository.findByUsername(username);

            if (existingUser.isPresent()) {
                User admin = existingUser.get();

                if (admin.getRole() != Role.ADMIN) {
                    throw new IllegalStateException(
                            "Configured admin username belongs to a regular user."
                    );
                }

                if (!passwordEncoder.matches(password, admin.getPassword())) {
                    admin.setPassword(passwordEncoder.encode(password));
                    userRepository.save(admin);
                }

                return;
            }

            if (userRepository.existsByEmail(email)) {
                throw new IllegalStateException(
                        "Configured admin email is already registered."
                );
            }

            User admin = new User(
                    username,
                    email,
                    passwordEncoder.encode(password),
                    Role.ADMIN
            );

            userRepository.save(admin);
        };
    }
}
