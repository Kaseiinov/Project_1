package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring",
        imports = {LocalDateTime.class})
public interface CardMapper {

    @Mapping(target = "ownerFirstName", source = "owner.firstName")
    @Mapping(target = "ownerLastName", source = "owner.lastName")
    CardResponseDto cardToCardResponseDto(Card card);

    Card toEntity(CreateCardRequestDto cardRequestDto);
}
