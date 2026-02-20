package br.com.msansone.aiandreia.service;

import br.com.msansone.aiandreia.dto.AiRequestDTO;
import br.com.msansone.aiandreia.dto.AiResponseDTO;
import br.com.msansone.aiandreia.entity.AiRequest;
import br.com.msansone.aiandreia.entity.User;
import br.com.msansone.aiandreia.integration.OllamaClient;
import br.com.msansone.aiandreia.integration.dto.OllamaChatMessage;
import br.com.msansone.aiandreia.repository.AiRequestRepository;
import br.com.msansone.aiandreia.repository.SettingRepository;
import br.com.msansone.aiandreia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRequestService {

    private final AiRequestRepository aiRequestRepository;
    private final UserRepository userRepository;
    private final SettingRepository settingRepository;
    private final OllamaClient ollamaClient;

    public AiResponseDTO processRequest(AiRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + requestDTO.userId()));

        // Build messages with context
        List<OllamaChatMessage> messages = new ArrayList<>();

        if (user.getResumo() != null && !user.getResumo().isBlank()) {
            // Use summary instead of full history to reduce tokens
            messages.add(new OllamaChatMessage("system",
                    "Resumo do contexto das conversas anteriores do usuário:\n" + user.getResumo()));
        } else {
            // No summary yet, fall back to full history
            List<AiRequest> history = aiRequestRepository.findByUserIdOrderByCreatedAtAsc(user.getId());
            for (AiRequest past : history) {
                messages.add(new OllamaChatMessage("user", past.getQuestion()));
                if (past.getAnswer() != null) {
                    messages.add(new OllamaChatMessage("assistant", past.getAnswer()));
                }
            }
        }

        settingRepository.findByKey("header").ifPresent(setting -> {
            if (setting.getValue() != null && !setting.getValue().isBlank()) {
                String headerValue = setting.getValue();
                if (headerValue.contains("<date>")) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    headerValue = headerValue.replace("<date>", LocalDateTime.now().format(formatter));
                }
                messages.add(new OllamaChatMessage("system", headerValue));
            }
        });

        messages.add(new OllamaChatMessage("user", requestDTO.question()));

        // Send to Ollama with context
        String answer = ollamaClient.chatWithHistory(requestDTO.model(), messages);

        // Save the new request with user association
        AiRequest entity = AiRequest.builder()
                .question(requestDTO.question())
                .model(requestDTO.model())
                .answer(answer)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        aiRequestRepository.save(entity);

        return new AiResponseDTO(answer);
    }
}
