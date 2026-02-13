package br.com.msansone.aiandreia.dto;

import jakarta.validation.constraints.NotBlank;

public record AiRequestDTO(
        @NotBlank(message = "question is required") String question,

        @NotBlank(message = "model is required") String model) {
}
