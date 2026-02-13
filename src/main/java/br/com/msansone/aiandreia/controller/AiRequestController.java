package br.com.msansone.aiandreia.controller;

import br.com.msansone.aiandreia.dto.AiRequestDTO;
import br.com.msansone.aiandreia.dto.AiResponseDTO;
import br.com.msansone.aiandreia.service.AiRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/aiandreia")
@RequiredArgsConstructor
public class AiRequestController {

    private final AiRequestService aiRequestService;

    @PostMapping("/request")
    public ResponseEntity<AiResponseDTO> handleRequest(@Valid @RequestBody AiRequestDTO requestDTO) {
        AiResponseDTO response = aiRequestService.processRequest(requestDTO);
        return ResponseEntity.ok(response);
    }
}
