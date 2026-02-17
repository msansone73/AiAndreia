package br.com.msansone.aiandreia.integration;

import br.com.msansone.aiandreia.integration.dto.OllamaChatApiResponse;
import br.com.msansone.aiandreia.integration.dto.OllamaChatMessage;
import br.com.msansone.aiandreia.integration.dto.OllamaChatRequest;
import br.com.msansone.aiandreia.integration.dto.OllamaChatResponse;
import br.com.msansone.aiandreia.integration.dto.OllamaGenerateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class OllamaClient {

    private final RestClient restClient;

    public OllamaClient(@Value("${ollama.api.url}") String ollamaUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(ollamaUrl)
                .build();
    }

    public String chat(String model, String userMessage) {
        OllamaGenerateRequest request = new OllamaGenerateRequest(model, userMessage, false);

        log.info("Sending request to Ollama - model: {}, prompt: {}", model, userMessage);

        OllamaChatResponse response = restClient.post()
                .uri("/api/generate")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OllamaChatResponse.class);

        log.info("Ollama response: {}", response);

        String answer = response != null ? response.response() : null;
        log.info("Extracted answer: {}", answer);

        return answer;
    }

    public String chatWithHistory(String model, List<OllamaChatMessage> messages) {
        OllamaChatRequest request = new OllamaChatRequest(model, messages, false);

        log.info("Sending chat request to Ollama - model: {}, messages count: {}", model, messages.size());

        OllamaChatApiResponse response = restClient.post()
                .uri("/api/chat")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(OllamaChatApiResponse.class);

        log.info("Ollama chat response: {}", response);

        String answer = (response != null && response.message() != null)
                ? response.message().content()
                : null;
        log.info("Extracted answer: {}", answer);

        return answer;
    }
}
