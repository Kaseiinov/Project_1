package com.example.bankcards.controller;

import com.example.bankcards.config.ApplicationConfig;
import com.example.bankcards.config.SecurityConfig;
import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.service.CardService;
import com.example.bankcards.service.ErrorService;
import com.example.bankcards.service.impl.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, ApplicationConfig.class})
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private ErrorService errorService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    private CardResponseDto mockCard() {
        CardResponseDto dto = new CardResponseDto();
        dto.setId(1L);
        dto.setCardNumberMasked("**** **** **** 1234");
        dto.setStatus(CardStatus.ACTIVE);
        dto.setBalance(BigDecimal.valueOf(1000));
        return dto;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllCards_success() throws Exception {
        when(cardService.getAllCards(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(mockCard())));

        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_success() throws Exception {
        CreateCardRequestDto request = new CreateCardRequestDto();
        request.setOwnerId(1L);
        request.setCardNumber("1234567812345678");
        request.setExpiryDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.ZERO);

        when(cardService.createCard(any())).thenReturn(mockCard());

        mockMvc.perform(post("/api/admin/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createCard_invalidRequest_returns400() throws Exception {
        CreateCardRequestDto request = new CreateCardRequestDto();

        mockMvc.perform(post("/api/admin/cards")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getCard_success() throws Exception {
        when(cardService.getCard(1L)).thenReturn(mockCard());

        mockMvc.perform(get("/api/admin/cards/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void blockCard_success() throws Exception {
        CardResponseDto blocked = mockCard();
        blocked.setStatus(CardStatus.BLOCKED);
        when(cardService.blockCard(1L)).thenReturn(blocked);

        mockMvc.perform(post("/api/admin/cards/1/block").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void activateCard_success() throws Exception {
        when(cardService.activateCard(1L)).thenReturn(mockCard());

        mockMvc.perform(post("/api/admin/cards/1/activate").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteCard_success() throws Exception {
        doNothing().when(cardService).deleteCard(1L);

        mockMvc.perform(delete("/api/admin/cards/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getAllCards_forbiddenForUser() throws Exception {
        mockMvc.perform(get("/api/admin/cards"))
                .andExpect(status().isForbidden());
    }
}
