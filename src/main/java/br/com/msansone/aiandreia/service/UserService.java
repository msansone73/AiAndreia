package br.com.msansone.aiandreia.service;

import br.com.msansone.aiandreia.dto.UserDTO;
import br.com.msansone.aiandreia.dto.UserResponseDTO;
import br.com.msansone.aiandreia.entity.AiRequest;
import br.com.msansone.aiandreia.entity.User;
import br.com.msansone.aiandreia.integration.OllamaClient;
import br.com.msansone.aiandreia.repository.AiRequestRepository;
import br.com.msansone.aiandreia.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AiRequestRepository aiRequestRepository;
    private final OllamaClient ollamaClient;

    public UserResponseDTO createUser(UserDTO dto) {
        User user = User.builder()
                .email(dto.email())
                .password(dto.password())
                .name(dto.name())
                .createdAt(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    public UserResponseDTO updateUser(Long id, UserDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setEmail(dto.email());
        user.setPassword(dto.password());
        user.setName(dto.name());

        User saved = userRepository.save(user);
        return toResponseDTO(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        aiRequestRepository.deleteByUserId(id);
        userRepository.deleteById(id);
    }

    public List<UserResponseDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return toResponseDTO(user);
    }

    @Transactional
    public void resetUserHistory(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        aiRequestRepository.deleteByUserId(id);
    }

    @Transactional
    public String summarizeUserHistory(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        List<AiRequest> history = aiRequestRepository.findByUserIdOrderByCreatedAtAsc(id);

        if (history.isEmpty()) {
            throw new RuntimeException("No conversation history found for user id: " + id);
        }

        // Build full conversation text
        StringBuilder sb = new StringBuilder();
        for (AiRequest req : history) {
            sb.append("Pergunta: ").append(req.getQuestion()).append("\n");
            if (req.getAnswer() != null) {
                sb.append("Resposta: ").append(req.getAnswer()).append("\n");
            }
            sb.append("\n");
        }

        // Ask Ollama to summarize
        String model = history.get(0).getModel();
        String prompt = "Faça um resumo compacto do seguinte histórico de conversas entre um usuário e uma IA. "
                + "O resumo deve preservar os pontos principais, decisões tomadas e contexto relevante, "
                + "mas de forma muito mais curta para economizar tokens em futuras interações.\n\n"
                + sb.toString();

        String resumo = ollamaClient.chat(model, prompt);

        // Save summary to user
        user.setResumo(resumo);
        userRepository.save(user);

        return resumo;
    }

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt());
    }
}
