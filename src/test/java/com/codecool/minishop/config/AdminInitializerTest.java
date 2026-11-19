package com.codecool.minishop.config;

import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
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
        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of());

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.empty());

        CommandLineRunner runner = createRunner(
                "admin", "admin@example.com", "AdminPassword123"
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
    void shouldNotCreateDuplicateAdminWhenConfigurationIsUnchanged()
            throws Exception {

        User existingAdmin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("AdminPassword123"),
                Role.ADMIN
        );

        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(existingAdmin));

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(existingAdmin));

        when(userRepository.findByEmail("admin@example.com"))
                .thenReturn(Optional.of(existingAdmin));

        CommandLineRunner runner = createRunner(
                "admin", "admin@example.com", "AdminPassword123"
        );

        runner.run();

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldUpdateExistingAdminWhenConfigurationChanges()
            throws Exception {

        User existingAdmin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("OldPassword123"),
                Role.ADMIN
        );

        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(existingAdmin));

        when(userRepository.findByUsername("superadmin"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("superadmin@example.com"))
                .thenReturn(Optional.empty());

        CommandLineRunner runner = createRunner(
                "superadmin",
                "superadmin@example.com",
                "NewPassword123"
        );

        runner.run();

        assertEquals("superadmin", existingAdmin.getUsername());
        assertEquals("superadmin@example.com", existingAdmin.getEmail());
        assertTrue(passwordEncoder.matches(
                "NewPassword123",
                existingAdmin.getPassword()
        ));

        verify(userRepository).save(existingAdmin);
    }

    @Test
    void shouldRejectUsernameAlreadyUsedByRegularUser() {
        User existingUser = new User(
                "taken",
                "user@example.com",
                passwordEncoder.encode("Password123"),
                Role.USER
        );

        User existingAdmin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("AdminPassword123"),
                Role.ADMIN
        );

        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(existingAdmin));

        when(userRepository.findByUsername("taken"))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.findByEmail("newadmin@example.com"))
                .thenReturn(Optional.empty());

        CommandLineRunner runner = createRunner(
                "taken",
                "newadmin@example.com",
                "NewPassword123"
        );

        assertThrows(IllegalStateException.class, runner::run);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectEmailAlreadyUsedByRegularUser() {
        User existingUser = new User(
                "regular",
                "taken@example.com",
                passwordEncoder.encode("Password123"),
                Role.USER
        );

        User existingAdmin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("AdminPassword123"),
                Role.ADMIN
        );

        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(existingAdmin));

        when(userRepository.findByUsername("newadmin"))
                .thenReturn(Optional.empty());

        when(userRepository.findByEmail("taken@example.com"))
                .thenReturn(Optional.of(existingUser));

        CommandLineRunner runner = createRunner(
                "newadmin",
                "taken@example.com",
                "NewPassword123"
        );

        assertThrows(IllegalStateException.class, runner::run);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectMultipleAdminAccounts() {
        User firstAdmin = new User(
                "admin1",
                "admin1@example.com",
                passwordEncoder.encode("Password123"),
                Role.ADMIN
        );

        User secondAdmin = new User(
                "admin2",
                "admin2@example.com",
                passwordEncoder.encode("Password123"),
                Role.ADMIN
        );

        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(firstAdmin, secondAdmin));

        CommandLineRunner runner = createRunner(
                "admin",
                "admin@example.com",
                "NewPassword123"
        );

        assertThrows(IllegalStateException.class, runner::run);

        verify(userRepository, never()).save(any(User.class));
    }

    private CommandLineRunner createRunner(
            String username,
            String email,
            String password) {

        return adminInitializer.createAdmin(
                userRepository,
                passwordEncoder,
                username,
                email,
                password
        );
    }
}
