package br.com.msansone.aiandreia.controller;

import br.com.msansone.aiandreia.dto.SettingDTO;
import br.com.msansone.aiandreia.dto.SettingResponseDTO;
import br.com.msansone.aiandreia.service.SettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/aiandreia/setting")
@RequiredArgsConstructor
public class SettingController {

    private final SettingService settingService;

    @PostMapping
    public ResponseEntity<SettingResponseDTO> createSetting(@Valid @RequestBody SettingDTO settingDTO) {
        SettingResponseDTO response = settingService.createSetting(settingDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SettingResponseDTO> updateSetting(@PathVariable Long id,
            @Valid @RequestBody SettingDTO settingDTO) {
        SettingResponseDTO response = settingService.updateSetting(id, settingDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSetting(@PathVariable Long id) {
        settingService.deleteSetting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<SettingResponseDTO>> listSettings() {
        List<SettingResponseDTO> settings = settingService.listSettings();
        return ResponseEntity.ok(settings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SettingResponseDTO> getSettingById(@PathVariable Long id) {
        SettingResponseDTO response = settingService.getSettingById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<SettingResponseDTO> getSettingByKey(@PathVariable String key) {
        SettingResponseDTO response = settingService.getSettingByKey(key);
        return ResponseEntity.ok(response);
    }
}
