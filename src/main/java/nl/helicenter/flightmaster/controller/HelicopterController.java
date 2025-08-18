package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.dto.HelicopterRequestDto;
import nl.helicenter.flightmaster.dto.HelicopterResponseDto;
import nl.helicenter.flightmaster.service.HelicopterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/helicopters")
public class HelicopterController {

    private final HelicopterService helicopterService;

    public HelicopterController(HelicopterService helicopterService) {
        this.helicopterService = helicopterService;
    }

    @PostMapping
    public ResponseEntity<HelicopterResponseDto> create(@Valid @RequestBody HelicopterRequestDto dto) {
        HelicopterResponseDto created = helicopterService.addHelicopter(dto);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<HelicopterResponseDto>> getAll() {
        return ResponseEntity.ok(helicopterService.getAllHelicopters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HelicopterResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(helicopterService.getHelicopterById(id));
    }
}
