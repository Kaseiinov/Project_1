package com.example.bankcards.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "Request to transfer funds between cards")
@Data
public class TransferRequestDto {
    @NotNull(message = "FromCardId cannot be empty")
    private Long fromCardId;

    @NotNull(message = "toCard cannot be empty")
    private Long toCardId;

    @NotNull(message = "Amount cannot be empty")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
}
