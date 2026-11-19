package com.codecool.minishop.config;

import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

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
            List<User> admins = userRepository.findAllByRole(Role.ADMIN);

            if (admins.size() > 1) {
                throw new IllegalStateException(
                        "Multiple admin accounts found. MiniShop expects exactly one admin account."
                );
            }

            if (admins.isEmpty()) {
                validateUsernameAndEmail(
                        userRepository, username, email, null
                );

                User admin = new User(
                        username,
                        email,
                        passwordEncoder.encode(password),
                        Role.ADMIN
                );

                userRepository.save(admin);
                return;
            }

            User admin = admins.getFirst();

            validateUsernameAndEmail(
                    userRepository, username, email, admin
            );

            boolean changed = false;

            if (!admin.getUsername().equals(username)) {
                admin.setUsername(username);
                changed = true;
            }

            if (!admin.getEmail().equals(email)) {
                admin.setEmail(email);
                changed = true;
            }

            if (!passwordEncoder.matches(password, admin.getPassword())) {
                admin.setPassword(passwordEncoder.encode(password));
                changed = true;
            }

            if (changed) {
                userRepository.save(admin);
            }
        };
    }

    private void validateUsernameAndEmail(
            UserRepository userRepository,
            String username,
            String email,
            User currentAdmin) {

        Optional<User> existingUsername =
                userRepository.findByUsername(username);

        if (existingUsername.isPresent()
                && isDifferentAccount(existingUsername.get(), currentAdmin)) {
            throw new IllegalStateException(
                    "Configured admin username is already used by another account."
            );
        }

        Optional<User> existingEmail =
                userRepository.findByEmail(email);

        if (existingEmail.isPresent()
                && isDifferentAccount(existingEmail.get(), currentAdmin)) {
            throw new IllegalStateException(
                    "Configured admin email is already used by another account."
            );
        }
    }

    private boolean isDifferentAccount(User user, User currentAdmin) {
        if (currentAdmin == null) {
            return true;
        }

        if (user == currentAdmin) {
            return false;
        }

        if (user.getId() != null && currentAdmin.getId() != null) {
            return !user.getId().equals(currentAdmin.getId());
        }

        return true;
    }
}
