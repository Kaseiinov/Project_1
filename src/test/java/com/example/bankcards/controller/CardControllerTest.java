package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.ErrorService;
import com.example.bankcards.service.impl.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CardController.class)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private ErrorService errorService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private JwtService jwtService;

    private CardResponseDto mockCard() {
        CardResponseDto dto = new CardResponseDto();
        dto.setId(1L);
        dto.setCardNumberMasked("**** **** **** 1234");
        dto.setStatus(CardStatus.ACTIVE);
        dto.setBalance(BigDecimal.valueOf(1000));
        return dto;
    }

    private User mockUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        return user;
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void getMyCards_success() throws Exception {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser()));
        when(cardService.getUserCards(eq(1L), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockCard())));

        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void getMyCards_filteredByStatus() throws Exception {
        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser()));
        when(cardService.getUserCards(eq(1L), eq(CardStatus.ACTIVE), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockCard())));

        mockMvc.perform(get("/api/cards").param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void transfer_success() throws Exception {
        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser()));

        mockMvc.perform(post("/api/cards/transfer")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "test@test.com", roles = "USER")
    void requestBlock_success() throws Exception {
        CardResponseDto blocked = mockCard();
        blocked.setStatus(CardStatus.BLOCKED);

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(mockUser()));
        when(cardService.requestBlock(1L, 1L)).thenReturn(blocked);

        mockMvc.perform(post("/api/cards/1/block").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    void getMyCards_unauthorized() throws Exception {
        mockMvc.perform(get("/api/cards"))
                .andExpect(status().isUnauthorized());
    }
}