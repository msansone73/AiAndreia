package br.com.msansone.aiandreia.integration.dto;

public record OllamaGenerateRequest(String model, String prompt, boolean stream) {
}
