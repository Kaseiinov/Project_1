package com.example.bankcards.dto.response;

import com.example.bankcards.entity.CardStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardResponseDto {
    private Long id;
    private String cardNumberMasked;
    private String ownerFirstName;
    private String ownerLastName;
    private LocalDate expiryDate;
    private CardStatus status;
    private BigDecimal balance;
}
