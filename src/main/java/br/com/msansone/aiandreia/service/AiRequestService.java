package br.com.msansone.aiandreia.service;

import br.com.msansone.aiandreia.dto.AiRequestDTO;
import br.com.msansone.aiandreia.dto.AiResponseDTO;
import br.com.msansone.aiandreia.entity.AiRequest;
import br.com.msansone.aiandreia.integration.OllamaClient;
import br.com.msansone.aiandreia.repository.AiRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AiRequestService {

    private final AiRequestRepository aiRequestRepository;
    private final OllamaClient ollamaClient;

    public AiResponseDTO processRequest(AiRequestDTO requestDTO) {
        String answer = ollamaClient.chat(requestDTO.model(), requestDTO.question());

        AiRequest entity = AiRequest.builder()
                .question(requestDTO.question())
                .model(requestDTO.model())
                .answer(answer)
                .createdAt(LocalDateTime.now())
                .build();

        aiRequestRepository.save(entity);

        return new AiResponseDTO(answer);
    }
}
