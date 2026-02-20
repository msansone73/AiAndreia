package br.com.msansone.aiandreia.dto;

import jakarta.validation.constraints.NotBlank;

public record SettingDTO(
        @NotBlank(message = "Key is required") String key,
        @NotBlank(message = "Value is required") String value) {
}
