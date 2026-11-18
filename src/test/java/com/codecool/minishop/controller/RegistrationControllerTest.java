package com.codecool.minishop.controller;

import com.codecool.minishop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegistrationController.class)
@AutoConfigureMockMvc(addFilters = false)
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    void shouldDisplayRegistrationPage() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeExists("registrationForm"));
    }

    @Test
    void shouldRegisterUserAndRedirectToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("email", "newuser@example.com")
                        .param("password", "Password123")
                        .param("confirmPassword", "Password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?registered"));

        verify(userService).registerUser(
                "newuser",
                "newuser@example.com",
                "Password123"
        );
    }

    @Test
    void shouldRejectInvalidRegistrationData() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "")
                        .param("email", "invalid-email")
                        .param("password", "short")
                        .param("confirmPassword", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "registrationForm",
                        "username",
                        "email",
                        "password"
                ));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldRejectRegistrationWhenPasswordsDoNotMatch() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "newuser")
                        .param("email", "newuser@example.com")
                        .param("password", "Password123")
                        .param("confirmPassword", "Different123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attributeHasFieldErrors(
                        "registrationForm",
                        "confirmPassword"
                ));

        verifyNoInteractions(userService);
    }

    @Test
    void shouldDisplayErrorWhenUsernameIsAlreadyTaken() throws Exception {
        when(userService.registerUser(
                "existinguser",
                "new@example.com",
                "Password123"
        )).thenThrow(new IllegalArgumentException(
                "Username is already taken."
        ));

        mockMvc.perform(post("/register")
                        .param("username", "existinguser")
                        .param("email", "new@example.com")
                        .param("password", "Password123")
                        .param("confirmPassword", "Password123"))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().attribute(
                        "registrationError",
                        "Username is already taken."
                ));
    }
}
