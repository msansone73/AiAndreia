package br.com.msansone.aiandreia.integration.dto;

import java.util.List;

public record OllamaChatRequest(String model, List<OllamaChatMessage> messages, boolean stream) {
}
