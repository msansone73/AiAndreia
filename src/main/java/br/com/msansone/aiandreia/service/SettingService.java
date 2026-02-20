package br.com.msansone.aiandreia.service;

import br.com.msansone.aiandreia.dto.SettingDTO;
import br.com.msansone.aiandreia.dto.SettingResponseDTO;
import br.com.msansone.aiandreia.entity.Setting;
import br.com.msansone.aiandreia.repository.SettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final SettingRepository settingRepository;

    public SettingResponseDTO createSetting(SettingDTO dto) {
        if (settingRepository.findByKey(dto.key()).isPresent()) {
            throw new RuntimeException("Setting with key already exists: " + dto.key());
        }

        Setting setting = Setting.builder()
                .key(dto.key())
                .value(dto.value())
                .build();

        Setting saved = settingRepository.save(setting);
        return toResponseDTO(saved);
    }

    public SettingResponseDTO updateSetting(Long id, SettingDTO dto) {
        Setting setting = settingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setting not found with id: " + id));

        setting.setKey(dto.key());
        setting.setValue(dto.value());

        Setting saved = settingRepository.save(setting);
        return toResponseDTO(saved);
    }

    public void deleteSetting(Long id) {
        if (!settingRepository.existsById(id)) {
            throw new RuntimeException("Setting not found with id: " + id);
        }
        settingRepository.deleteById(id);
    }

    public List<SettingResponseDTO> listSettings() {
        return settingRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public SettingResponseDTO getSettingById(Long id) {
        Setting setting = settingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setting not found with id: " + id));
        return toResponseDTO(setting);
    }

    public SettingResponseDTO getSettingByKey(String key) {
        Setting setting = settingRepository.findByKey(key)
                .orElseThrow(() -> new RuntimeException("Setting not found with key: " + key));
        return toResponseDTO(setting);
    }

    private SettingResponseDTO toResponseDTO(Setting setting) {
        return new SettingResponseDTO(
                setting.getId(),
                setting.getKey(),
                setting.getValue());
    }
}
