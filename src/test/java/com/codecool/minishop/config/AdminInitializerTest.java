package com.codecool.minishop.config;

import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminInitializerTest {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private AdminInitializer adminInitializer;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        adminInitializer = new AdminInitializer();
    }

    @Test
    void shouldCreateAdminWhenAccountDoesNotExist() throws Exception {
        when(userRepository.existsByUsername("admin"))
                .thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com"))
                .thenReturn(false);

        CommandLineRunner runner = adminInitializer.createAdmin(
                userRepository,
                passwordEncoder,
                "admin",
                "admin@example.com",
                "AdminPassword123"
        );

        runner.run();

        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("admin")
                        && user.getEmail().equals("admin@example.com")
                        && user.getRole() == Role.ADMIN
                        && passwordEncoder.matches(
                        "AdminPassword123",
                        user.getPassword()
                )
        ));
    }

    @Test
    void shouldNotCreateDuplicateAdmin() throws Exception {
        User existingAdmin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("AdminPassword123"),
                Role.ADMIN
        );

        when(userRepository.existsByUsername("admin"))
                .thenReturn(true);
        when(userRepository.findByUsername("admin"))
                .thenReturn(java.util.Optional.of(existingAdmin));

        CommandLineRunner runner = adminInitializer.createAdmin(
                userRepository,
                passwordEncoder,
                "admin",
                "admin@example.com",
                "AdminPassword123"
        );

        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectUsernameAlreadyUsedByRegularUser() {
        User existingUser = new User(
                "admin",
                "user@example.com",
                passwordEncoder.encode("Password123"),
                Role.USER
        );

        when(userRepository.existsByUsername("admin"))
                .thenReturn(true);
        when(userRepository.findByUsername("admin"))
                .thenReturn(java.util.Optional.of(existingUser));

        CommandLineRunner runner = adminInitializer.createAdmin(
                userRepository,
                passwordEncoder,
                "admin",
                "admin@example.com",
                "AdminPassword123"
        );

        assertThrows(
                IllegalStateException.class,
                runner::run
        );

        verify(userRepository, never()).save(any(User.class));
    }
}
