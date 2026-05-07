package com.example.bankcards.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CreateCardRequestDto {
    @NotNull(message = "Owner id cannot be empty")
    private Long ownerId;

    @NotBlank(message = "Card number cannot be empty")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull
    @Future
    private LocalDate expiryDate;

    @DecimalMin("0.0")
    private BigDecimal balance = BigDecimal.ZERO;
}
