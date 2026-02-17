package br.com.msansone.aiandreia.service;

import br.com.msansone.aiandreia.dto.AiRequestDTO;
import br.com.msansone.aiandreia.dto.AiResponseDTO;
import br.com.msansone.aiandreia.entity.AiRequest;
import br.com.msansone.aiandreia.entity.User;
import br.com.msansone.aiandreia.integration.OllamaClient;
import br.com.msansone.aiandreia.integration.dto.OllamaChatMessage;
import br.com.msansone.aiandreia.repository.AiRequestRepository;
import br.com.msansone.aiandreia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiRequestService {

    private final AiRequestRepository aiRequestRepository;
    private final UserRepository userRepository;
    private final OllamaClient ollamaClient;

    public AiResponseDTO processRequest(AiRequestDTO requestDTO) {
        User user = userRepository.findById(requestDTO.userId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + requestDTO.userId()));

        // Load user's conversation history
        List<AiRequest> history = aiRequestRepository.findByUserIdOrderByCreatedAtAsc(user.getId());

        // Build messages array with history + current question
        List<OllamaChatMessage> messages = new ArrayList<>();
        for (AiRequest past : history) {
            messages.add(new OllamaChatMessage("user", past.getQuestion()));
            if (past.getAnswer() != null) {
                messages.add(new OllamaChatMessage("assistant", past.getAnswer()));
            }
        }
        messages.add(new OllamaChatMessage("user", requestDTO.question()));

        // Send to Ollama with full history context
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
