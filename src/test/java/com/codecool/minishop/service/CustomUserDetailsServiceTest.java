package com.codecool.minishop.service;

import com.codecool.minishop.model.Role;
import com.codecool.minishop.model.User;
import com.codecool.minishop.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    private UserRepository userRepository;
    private CustomUserDetailsService userDetailsService;
    private BCryptPasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        String encodedPassword = passwordEncoder.encode("Password123");

        User user = new User(
                "testuser",
                "test@example.com",
                encodedPassword,
                Role.USER
        );

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        UserDetails result =
                userDetailsService.loadUserByUsername("testuser");

        assertEquals("testuser", result.getUsername());
        assertEquals(encodedPassword, result.getPassword());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_USER")
                ));

        assertTrue(passwordEncoder.matches(
                "Password123",
                result.getPassword()
        ));
    }

    @Test
    void shouldLoadAdminWithAdminRole() {
        User admin = new User(
                "admin",
                "admin@example.com",
                passwordEncoder.encode("AdminPassword123"),
                Role.ADMIN
        );

        when(userRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        UserDetails result =
                userDetailsService.loadUserByUsername("admin");

        assertEquals("admin", result.getUsername());
        assertTrue(result.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                ));
    }

    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("unknown")
        );
    }
}
