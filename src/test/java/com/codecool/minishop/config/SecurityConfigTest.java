package com.codecool.minishop.config;

import com.codecool.minishop.service.CustomUserDetailsService;
import com.codecool.minishop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void unauthenticatedUserShouldBeRedirectedToLoginWhenAccessingAdminPage() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void regularUserShouldNotAccessAdminPage() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("regularUser").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminShouldAccessUserManagementPage() throws Exception {
        mockMvc.perform(get("/admin/users")
                        .with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void regularUserShouldPassSecurityWhenAccessingCart() throws Exception {
        mockMvc.perform(get("/cart")
                        .with(user("regularUser").roles("USER")))
                .andExpect(status().isNotFound());
    }
}
