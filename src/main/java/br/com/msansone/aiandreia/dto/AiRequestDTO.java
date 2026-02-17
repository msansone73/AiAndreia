package br.com.msansone.aiandreia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AiRequestDTO(
                @NotBlank(message = "question is required") String question,

                @NotBlank(message = "model is required") String model,

                @NotNull(message = "userId is required") Long userId) {
}
