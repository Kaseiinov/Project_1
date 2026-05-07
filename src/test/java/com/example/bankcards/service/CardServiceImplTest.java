package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.exceptions.BadRequestException;
import com.example.bankcards.exception.exceptions.NotFoundException;
import com.example.bankcards.mapper.CardMapper;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.CardEncryptionService;
import com.example.bankcards.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock private CardRepository cardRepository;
    @Mock private UserRepository userRepository;
    @Mock private CardEncryptionService encryptionService;
    @Mock private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card activeCard;
    private CardResponseDto cardResponseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        activeCard = Card.builder()
                .id(1L)
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(1000))
                .build();

        cardResponseDto = new CardResponseDto();
        cardResponseDto.setId(1L);
    }

    @Test
    void createCard_success() {
        CreateCardRequestDto request = new CreateCardRequestDto();
        request.setOwnerId(1L);
        request.setCardNumber("1234567812345678");
        request.setExpiryDate(LocalDate.now().plusYears(2));
        request.setBalance(BigDecimal.ZERO);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(encryptionService.encrypt(any())).thenReturn("encrypted");
        when(encryptionService.mask(any())).thenReturn("**** **** **** 5678");
        when(cardRepository.save(any())).thenReturn(activeCard);
        when(cardMapper.cardToCardResponseDto(any())).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.createCard(request);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(cardRepository).save(any(Card.class));
    }

    @Test
    void createCard_userNotFound_throwsNotFoundException() {
        CreateCardRequestDto request = new CreateCardRequestDto();
        request.setOwnerId(99L);

        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.createCard(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
    }

    @Test
    void blockCard_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.save(any())).thenReturn(activeCard);
        when(cardMapper.cardToCardResponseDto(any())).thenReturn(cardResponseDto);

        cardService.blockCard(1L);

        assertThat(activeCard.getStatus()).isEqualTo(CardStatus.BLOCKED);
        verify(cardRepository).save(activeCard);
    }

    @Test
    void blockCard_cardNotFound_throwsNotFoundException() {
        when(cardRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardService.blockCard(99L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Card not found");
    }

    @Test
    void activateCard_success() {
        activeCard.setStatus(CardStatus.BLOCKED);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.save(any())).thenReturn(activeCard);
        when(cardMapper.cardToCardResponseDto(any())).thenReturn(cardResponseDto);

        cardService.activateCard(1L);

        assertThat(activeCard.getStatus()).isEqualTo(CardStatus.ACTIVE);
        verify(cardRepository).save(activeCard);
    }


    @Test
    void transfer_success() {
        Card toCard = Card.builder()
                .id(2L)
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(500))
                .build();

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(200));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        cardService.transfer(request, 1L);

        assertThat(activeCard.getBalance()).isEqualByComparingTo("800");
        assertThat(toCard.getBalance()).isEqualByComparingTo("700");
        verify(cardRepository, times(2)).save(any(Card.class));
    }

    @Test
    void transfer_insufficientBalance_throwsBadRequestException() {
        Card toCard = Card.builder()
                .id(2L)
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(9999));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThatThrownBy(() -> cardService.transfer(request, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Insufficient balance");
    }

    @Test
    void transfer_blockedCard_throwsBadRequestException() {
        activeCard.setStatus(CardStatus.BLOCKED);

        Card toCard = Card.builder()
                .id(2L)
                .owner(user)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.ZERO)
                .build();

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(toCard));

        assertThatThrownBy(() -> cardService.transfer(request, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Both cards must be active");
    }

    @Test
    void transfer_cardNotOwnedByUser_throwsBadRequestException() {
        User otherUser = new User();
        otherUser.setId(2L);

        Card otherCard = Card.builder()
                .id(2L)
                .owner(otherUser)
                .status(CardStatus.ACTIVE)
                .balance(BigDecimal.valueOf(500))
                .build();

        TransferRequestDto request = new TransferRequestDto();
        request.setFromCardId(1L);
        request.setToCardId(2L);
        request.setAmount(BigDecimal.valueOf(100));

        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.findById(2L)).thenReturn(Optional.of(otherCard));

        assertThatThrownBy(() -> cardService.transfer(request, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("Cards must belong to the same user");
    }


    @Test
    void requestBlock_success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));
        when(cardRepository.save(any())).thenReturn(activeCard);
        when(cardMapper.cardToCardResponseDto(any())).thenReturn(cardResponseDto);

        cardService.requestBlock(1L, 1L);

        assertThat(activeCard.getStatus()).isEqualTo(CardStatus.BLOCKED);
    }

    @Test
    void requestBlock_notOwner_throwsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(activeCard));

        assertThatThrownBy(() -> cardService.requestBlock(1L, 99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Access denied");
    }


    @Test
    void deleteCard_success() {
        cardService.deleteCard(1L);
        verify(cardRepository).deleteById(1L);
    }
}
