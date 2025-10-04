package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.FlightRequestDto;
import nl.helicenter.flightmaster.dto.FlightResponseDto;
import nl.helicenter.flightmaster.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/flights")
@Validated
public class FlightController {
    private final FlightService flightService;
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<FlightResponseDto> create(@Valid @RequestBody FlightRequestDto dto) {
        FlightResponseDto created = flightService.create(dto);
        return ResponseEntity.status(HttpStatusCode.valueOf(201)).body(created);
    }

    @GetMapping
    public ResponseEntity<List<FlightResponseDto>> getAll() { return ResponseEntity.ok(flightService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDto> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(flightService.getById(id));
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        flightService.delete(id);
    }
}
