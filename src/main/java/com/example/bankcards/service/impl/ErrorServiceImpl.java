package com.example.bankcards.service.impl;

import com.example.bankcards.dto.response.ErrorResponseBody;
import com.example.bankcards.service.ErrorService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

@Service
public class ErrorServiceImpl implements ErrorService {

    @Override
    public ErrorResponseBody makeResponse(Exception e) {
        String error = e.getMessage();
        return ErrorResponseBody.builder()
                .title(error)
                .response(Map.of("errors", List.of(error)))
                .build();
    }

    @Override
    public ErrorResponseBody makeResponse(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        return ErrorResponseBody.builder()
                .title("Validation failed")
                .response(Map.of("errors", errors))
                .build();
    }

}
