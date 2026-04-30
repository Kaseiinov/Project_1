package com.example.bankcards.service;

import com.example.bankcards.dto.request.SignInRequestDto;
import com.example.bankcards.dto.request.SignUpRequestDto;
import com.example.bankcards.dto.response.JwtAuthResponse;

public interface AuthService {
    void signUp(SignUpRequestDto request);

    JwtAuthResponse signIn(SignInRequestDto request);
}
