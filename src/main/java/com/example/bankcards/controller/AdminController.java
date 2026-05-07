package com.example.bankcards.controller;

import com.example.bankcards.dto.request.CreateCardRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Admin", description = "Admin card management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final CardService cardService;

    @Operation(summary = "Get all cards", description = "Returns paginated list of all cards")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/cards")
    public ResponseEntity<Page<CardResponseDto>> getAllCards(Pageable pageable) {
        return ResponseEntity.ok(cardService.getAllCards(pageable));
    }

    @Operation(summary = "Create card", description = "Creates a new bank card for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Card created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    @PostMapping("/cards")
    public ResponseEntity<CardResponseDto> createCard(@Valid @RequestBody CreateCardRequestDto request) {
        return ResponseEntity.ok(cardService.createCard(request));
    }

    @Operation(summary = "Get card by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/cards/{id}")
    public ResponseEntity<CardResponseDto> getCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.getCard(id));
    }

    @Operation(summary = "Block card", description = "Sets card status to BLOCKED")
    @ApiResponse(responseCode = "200", description = "Card blocked")
    @PostMapping("/cards/{id}/block")
    public ResponseEntity<CardResponseDto> blockCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.blockCard(id));
    }

    @Operation(summary = "Activate card", description = "Sets card status to ACTIVE")
    @ApiResponse(responseCode = "200", description = "Card activated")
    @PostMapping("/cards/{id}/activate")
    public ResponseEntity<CardResponseDto> activateCard(@PathVariable Long id) {
        return ResponseEntity.ok(cardService.activateCard(id));
    }

    @Operation(summary = "Delete card")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Card deleted"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @DeleteMapping("/cards/{id}")
    public ResponseEntity<Void> deleteCard(@PathVariable Long id) {
        cardService.deleteCard(id);
        return ResponseEntity.noContent().build();
    }

}
