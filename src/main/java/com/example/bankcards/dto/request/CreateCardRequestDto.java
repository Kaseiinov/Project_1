package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Request to create a new bank card")
@Data
public class CreateCardRequestDto {
    @NotNull(message = "Owner id cannot be empty")
    private Long ownerId;

    @Schema(description = "16-digit card number", example = "1234567812345678")
    @NotBlank(message = "Card number cannot be empty")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @NotNull
    @Future
    private LocalDate expiryDate;

    @DecimalMin("0.0")
    private BigDecimal balance = BigDecimal.ZERO;
}
