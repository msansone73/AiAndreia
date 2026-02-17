package br.com.msansone.aiandreia.integration.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaChatApiResponse(
        String model,

        @JsonProperty("created_at") String createdAt,

        OllamaChatMessage message,

        Boolean done,

        @JsonProperty("done_reason") String doneReason,

        @JsonProperty("total_duration") Long totalDuration,

        @JsonProperty("eval_count") Integer evalCount) {
}
