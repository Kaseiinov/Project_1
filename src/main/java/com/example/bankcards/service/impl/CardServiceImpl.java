package com.example.bankcards.service.impl;

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
import com.example.bankcards.service.CardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserRepository userRepository;
    private final CardEncryptionService encryptionService;
    private final CardMapper cardMapper;

    @Override
    public CardResponseDto createCard(CreateCardRequestDto request) {
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new NotFoundException("User not found"));

        String encrypted = encryptionService.encrypt(request.getCardNumber());
        String masked = encryptionService.mask(request.getCardNumber());

        Card card = Card.builder()
                .cardNumberEncrypted(encrypted)
                .cardNumberMasked(masked)
                .owner(owner)
                .expiryDate(request.getExpiryDate())
                .status(CardStatus.ACTIVE)
                .balance(request.getBalance())
                .build();

        return toResponse(cardRepository.save(card));
    }

    @Override
    public Page<CardResponseDto> getUserCards(Long userId, CardStatus status, Pageable pageable) {
        if (status != null) {
            return cardRepository.findByOwnerIdAndStatus(userId, status, pageable).map(this::toResponse);
        }
        return cardRepository.findByOwnerId(userId, pageable).map(this::toResponse);
    }

    @Override
    public Page<CardResponseDto> getAllCards(Pageable pageable) {
        return cardRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public CardResponseDto getCard(Long cardId) {
        return toResponse(findCard(cardId));
    }

    @Override
    public CardResponseDto blockCard(Long cardId) {
        Card card = findCard(cardId);
        card.setStatus(CardStatus.BLOCKED);
        return toResponse(cardRepository.save(card));
    }

    @Override
    public CardResponseDto activateCard(Long cardId) {
        Card card = findCard(cardId);
        card.setStatus(CardStatus.ACTIVE);
        return toResponse(cardRepository.save(card));
    }

    @Override
    public void deleteCard(Long cardId) {
        cardRepository.deleteById(cardId);
    }

    @Transactional
    @Override
    public void transfer(TransferRequestDto request, Long userId) {
        Card from = findCard(request.getFromCardId());
        Card to = findCard(request.getToCardId());

        if (!from.getOwner().getId().equals(userId) || !to.getOwner().getId().equals(userId))
            throw new BadRequestException("Cards must belong to the same user");

        if (from.getStatus() != CardStatus.ACTIVE || to.getStatus() != CardStatus.ACTIVE)
            throw new BadRequestException("Both cards must be active");

        if (from.getBalance().compareTo(request.getAmount()) < 0)
            throw new BadRequestException("Insufficient balance");

        from.setBalance(from.getBalance().subtract(request.getAmount()));
        to.setBalance(to.getBalance().add(request.getAmount()));

        cardRepository.save(from);
        cardRepository.save(to);
    }

    @Override
    public CardResponseDto requestBlock(Long cardId, Long userId) {
        Card card = findCard(cardId);
        if (!card.getOwner().getId().equals(userId))
            throw new RuntimeException("Access denied");
        card.setStatus(CardStatus.BLOCKED);
        return toResponse(cardRepository.save(card));
    }

    @Override
    public Card findCard(Long id) {
        return cardRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Card not found"));
    }

    private CardResponseDto toResponse(Card card) {
        return cardMapper.cardToCardResponseDto(card);
    }


}
