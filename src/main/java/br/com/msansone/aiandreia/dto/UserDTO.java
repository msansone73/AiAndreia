package br.com.msansone.aiandreia.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        @NotBlank(message = "email is required") @Email(message = "email must be valid") String email,

        @NotBlank(message = "password is required") String password,

        @NotBlank(message = "name is required") String name) {
}
