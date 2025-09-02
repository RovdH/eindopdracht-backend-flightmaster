package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.dto.EventResponseDto;
import nl.helicenter.flightmaster.dto.FlightRequestDto;
import nl.helicenter.flightmaster.dto.FlightResponseDto;
import nl.helicenter.flightmaster.service.FlightService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flights")
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

    @GetMapping ResponseEntity<List<FlightResponseDto>> getAll() { return ResponseEntity.ok(flightService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlightResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(flightService.getById(id));
    }
}
