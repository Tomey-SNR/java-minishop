package com.codecool.minishop.service;

import com.codecool.minishop.dto.UserResponse;
import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        User savedUser = new User(
                "tomey",
                "tomey@example.com",
                passwordEncoder.encode("Password123"),
                Role.USER
        );

        when(userRepository.existsByUsername("tomey")).thenReturn(false);
        when(userRepository.existsByEmail("tomey@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(
                "tomey",
                "tomey@example.com",
                "Password123"
        );

        assertEquals("tomey", result.getUsername());
        assertEquals("tomey@example.com", result.getEmail());
        assertEquals(Role.USER, result.getRole());

        assertNotEquals("Password123", result.getPassword());
        assertTrue(passwordEncoder.matches("Password123", result.getPassword()));

        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateUsername() {
        when(userRepository.existsByUsername("tomey")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(
                        "tomey",
                        "another@example.com",
                        "Password123"
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("tomey@example.com")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.registerUser(
                        "newuser",
                        "tomey@example.com",
                        "Password123"
                )
        );

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUserResponsesShouldNotExposePasswords() {
        User admin = new User(
                "admin",
                "admin@minishop.local",
                "admin-hashed-password",
                Role.ADMIN
        );

        User regularUser = new User(
                "tomey",
                "tomey@example.com",
                "user-hashed-password",
                Role.USER
        );

        when(userRepository.findAll()).thenReturn(List.of(admin, regularUser));

        List<UserResponse> result = userService.getAllUserResponses();

        assertEquals(2, result.size());
        assertEquals("admin", result.get(0).username());
        assertEquals(Role.ADMIN, result.get(0).role());
        assertEquals("tomey", result.get(1).username());
        assertEquals(Role.USER, result.get(1).role());

        assertFalse(result.toString().contains("admin-hashed-password"));
        assertFalse(result.toString().contains("user-hashed-password"));
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        User user = new User(
                "tomey",
                "tomey@example.com",
                "hashed-password",
                Role.USER
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deleteUser(2L, "admin");

        verify(userRepository).delete(user);
    }

    @Test
    void shouldPreventDeletingCurrentUser() {
        User user = new User(
                "admin",
                "admin@minishop.local",
                "hashed-password",
                Role.ADMIN
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(1L, "admin")
        );

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void shouldPreventDeletingLastAdministrator() {
        User admin = new User(
                "other-admin",
                "other-admin@minishop.local",
                "hashed-password",
                Role.ADMIN
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(admin));
        when(userRepository.findAllByRole(Role.ADMIN)).thenReturn(List.of(admin));

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.deleteUser(2L, "admin")
        );

        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void shouldAllowDeletingAdministratorWhenAnotherAdminExists() {
        User adminToDelete = new User(
                "other-admin",
                "other-admin@minishop.local",
                "hashed-password",
                Role.ADMIN
        );

        User remainingAdmin = new User(
                "admin",
                "admin@minishop.local",
                "another-hashed-password",
                Role.ADMIN
        );

        when(userRepository.findById(2L)).thenReturn(Optional.of(adminToDelete));
        when(userRepository.findAllByRole(Role.ADMIN))
                .thenReturn(List.of(adminToDelete, remainingAdmin));

        userService.deleteUser(2L, "admin");

        verify(userRepository).delete(adminToDelete);
    }
}
