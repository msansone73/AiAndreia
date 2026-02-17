package br.com.msansone.aiandreia.service;

import br.com.msansone.aiandreia.dto.UserDTO;
import br.com.msansone.aiandreia.dto.UserResponseDTO;
import br.com.msansone.aiandreia.entity.User;
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

    private UserResponseDTO toResponseDTO(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getCreatedAt());
    }
}
