package com.example.bankcards.service;

import com.example.bankcards.dto.request.SignInRequestDto;
import com.example.bankcards.dto.request.SignUpRequestDto;
import com.example.bankcards.dto.response.JwtAuthResponse;
import com.example.bankcards.exception.exceptions.AlreadyExistException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.impl.AuthServiceImpl;
import com.example.bankcards.service.impl.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtService jwtService;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void signUp_success() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setFirstName("Jon");
        request.setLastName("Doe");
        request.setEmail("test@test.com");
        request.setPassword("Islam6002");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        authService.signUp(request);

        verify(userRepository).save(any());
    }

    @Test
    void signUp_emailAlreadyExists_throwsAlreadyExistException() {
        SignUpRequestDto request = new SignUpRequestDto();
        request.setEmail("test@test.com");

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.signUp(request))
                .isInstanceOf(AlreadyExistException.class)
                .hasMessage("Email Already Exist");
    }

    @Test
    void signIn_success() {
        SignInRequestDto request = new SignInRequestDto();
        request.setEmail("test@test.com");
        request.setPassword("password");

        when(jwtService.generateToken("test@test.com")).thenReturn("jwt-token");

        JwtAuthResponse response = authService.signIn(request);

        assertThat(response.getToken()).isEqualTo("jwt-token");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
