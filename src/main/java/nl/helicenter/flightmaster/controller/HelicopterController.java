package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.HelicopterRequestDto;
import nl.helicenter.flightmaster.dto.HelicopterResponseDto;
import nl.helicenter.flightmaster.dto.HelicopterUpdateDto;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.service.HelicopterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/helicopters")
@Validated
public class HelicopterController {

    private final HelicopterService helicopterService;

    public HelicopterController(HelicopterService helicopterService) {
        this.helicopterService = helicopterService;
    }

    @PostMapping
    public ResponseEntity<HelicopterResponseDto> create(@Valid @RequestBody HelicopterRequestDto dto) {
        HelicopterResponseDto created = helicopterService.addHelicopter(dto);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(created);
    }

    @GetMapping
    public ResponseEntity<List<HelicopterResponseDto>> getAll() {
        return ResponseEntity.ok(helicopterService.getAllHelicopters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HelicopterResponseDto> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(helicopterService.getHelicopterById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<HelicopterResponseDto> patch(
            @PathVariable @Positive Long id,
            @RequestBody @Valid HelicopterUpdateDto dto) {
        HelicopterResponseDto updated = helicopterService.patch(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        helicopterService.delete(id);
    }
}
