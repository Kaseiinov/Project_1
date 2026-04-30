package com.example.bankcards.service;

import com.example.bankcards.dto.response.ErrorResponseBody;
import org.springframework.web.bind.MethodArgumentNotValidException;

public interface ErrorService {
    ErrorResponseBody makeResponse(Exception e);

    ErrorResponseBody makeResponse(MethodArgumentNotValidException e);
}
