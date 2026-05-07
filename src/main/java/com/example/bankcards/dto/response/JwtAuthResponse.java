package com.example.bankcards.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Schema(description = "Jwt response object")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class JwtAuthResponse {
    private String token;
}
