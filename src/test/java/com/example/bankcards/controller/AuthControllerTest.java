package com.example.bankcards.controller;

import com.example.bankcards.config.ApplicationConfig;
import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.request.SignInRequestDto;
import com.example.bankcards.dto.request.SignUpRequestDto;
import com.example.bankcards.dto.response.JwtAuthResponse;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.ErrorService;
import com.example.bankcards.service.impl.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, ApplicationConfig.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private ErrorService errorService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Test
    void signUp_success() throws Exception {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setFirstName("Jon");
        request.setLastName("Doe");
        request.setEmail("test@test.com");
        request.setPassword("Islam6002");

        mockMvc.perform(post("/api/auth/sign-up")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void signIn_success() throws Exception {
        SignInRequestDto request = new SignInRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("password123");

        when(authService.signIn(any())).thenReturn(new JwtAuthResponse("jwt-token"));

        mockMvc.perform(post("/api/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void signIn_invalidRequest_returns400() throws Exception {
        SignInRequestDto request = new SignInRequestDto();

        mockMvc.perform(post("/api/auth/sign-in")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}