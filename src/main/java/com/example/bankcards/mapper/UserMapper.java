package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.SignUpRequestDto;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        imports = {LocalDateTime.class, Role.class})
public interface UserMapper {

    @Mapping(target = "createdAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "role", expression = "java(Role.ROLE_USER)")
    @Mapping(target = "id", ignore = true)
    User toEntity(SignUpRequestDto signUpRequestDto);
}

