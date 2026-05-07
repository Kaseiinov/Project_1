package com.example.bankcards.service;

import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.CardStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    CardResponseDto createCard(CreateCardRequestDto request);

    Page<CardResponseDto> getUserCards(Long userId, CardStatus status, Pageable pageable);

    Page<CardResponseDto> getAllCards(Pageable pageable);

    CardResponseDto getCard(Long cardId);

    CardResponseDto blockCard(Long cardId);

    CardResponseDto activateCard(Long cardId);

    void deleteCard(Long cardId);

    @Transactional
    void transfer(TransferRequestDto request, Long userId);

    CardResponseDto requestBlock(Long cardId, Long userId);

    Card findCard(Long id);
}
