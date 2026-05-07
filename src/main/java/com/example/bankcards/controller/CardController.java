package com.example.bankcards.controller;

import com.example.bankcards.dto.request.TransferRequestDto;
import com.example.bankcards.dto.response.CardResponseDto;
import com.example.bankcards.entity.CardStatus;
import com.example.bankcards.repository.UserRepository;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cards", description = "User card operations")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;
    private final UserRepository userRepository;

    @Operation(summary = "Get my cards", description = "Returns paginated list of authenticated user's cards, optionally filtered by status")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public ResponseEntity<Page<CardResponseDto>> getMyCards(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) CardStatus status,
            Pageable pageable) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(cardService.getUserCards(userId, status, pageable));
    }

    @Operation(summary = "Transfer between cards", description = "Transfer funds between two cards owned by the authenticated user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transfer successful"),
            @ApiResponse(responseCode = "400", description = "Insufficient funds or invalid cards")
    })
    @PostMapping("/transfer")
    public ResponseEntity<Void> transfer(
            @Valid @RequestBody TransferRequestDto request,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        cardService.transfer(request, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Request card block", description = "User requests their own card to be blocked")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Block request submitted"),
            @ApiResponse(responseCode = "403", description = "Card does not belong to user")
    })
    @PostMapping("/{id}/block")
    public ResponseEntity<CardResponseDto> requestBlock(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserId(userDetails);
        return ResponseEntity.ok(cardService.requestBlock(id, userId));
    }

    private Long getUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}
