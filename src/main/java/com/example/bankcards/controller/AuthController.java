package com.example.bankcards.controller;

import com.example.bankcards.dto.request.SignInRequestDto;
import com.example.bankcards.dto.request.SignUpRequestDto;
import com.example.bankcards.dto.response.JwtAuthResponse;
import com.example.bankcards.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public void signUp(@RequestBody @Valid SignUpRequestDto request){
        authService.signUp(request);
    }

    @PostMapping("/sign-in")
    public JwtAuthResponse login(@RequestBody @Valid SignInRequestDto request) {
        return authService.signIn(request);
    }
}
