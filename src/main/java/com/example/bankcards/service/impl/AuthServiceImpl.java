package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.SignInRequestDto;
import com.example.bankcards.dto.request.SignUpRequestDto;
import com.example.bankcards.dto.response.JwtAuthResponse;
import com.example.bankcards.exception.exceptions.AlreadyExistException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.AuthService;
import com.example.bankcards.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public void signUp(SignUpRequestDto request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AlreadyExistException("Email Already Exist");
        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(userMapper.toEntity(request));
    }

    @Override
    public JwtAuthResponse signIn(SignInRequestDto request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
        ));

//        UserDetails user = userService
//                .userDetailsService()
//                .loadUserByUsername(request.getEmail());

        String token = jwtService.generateToken(request.getEmail());
        return new JwtAuthResponse(token);

    }
}
