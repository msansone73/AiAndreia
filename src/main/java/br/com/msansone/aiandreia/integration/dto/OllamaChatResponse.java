package br.com.msansone.aiandreia.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaChatResponse(
        String model,

        @JsonProperty("created_at") String createdAt,

        String response,

        List<Choice> choices,

        Boolean done,

        @JsonProperty("done_reason") String doneReason,

        @JsonProperty("total_duration") Long totalDuration,

        @JsonProperty("eval_count") Integer evalCount) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(OllamaChatMessage message) {
    }

    /**
     * Extracts the answer text, handling both native Ollama and OpenAI-compatible
     * formats.
     */
    public String extractAnswer() {
        if (choices != null && !choices.isEmpty() && choices.getFirst().message() != null) {
            return choices.getFirst().message().content();
        }
        return response;
    }
}
