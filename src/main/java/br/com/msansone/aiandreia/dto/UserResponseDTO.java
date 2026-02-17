package br.com.msansone.aiandreia.dto;

import java.time.LocalDateTime;

public record UserResponseDTO(
        Long id,
        String email,
        String name,
        LocalDateTime createdAt) {
}
