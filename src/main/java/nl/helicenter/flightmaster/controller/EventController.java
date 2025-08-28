package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.dto.EventRequestDto;
import nl.helicenter.flightmaster.dto.EventResponseDto;
import nl.helicenter.flightmaster.dto.FlightResponseDto;
import nl.helicenter.flightmaster.service.EventService;
import nl.helicenter.flightmaster.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final FlightService flightService;

    public EventController(EventService eventService, FlightService flightService) {
        this.eventService = eventService;
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<EventResponseDto> create(@Valid @RequestBody EventRequestDto dto) {
        EventResponseDto created = eventService.createEvent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/{id}/flights/generate")
    public ResponseEntity<List<FlightResponseDto>> generate(
            @PathVariable("id") Long eventId,
            @RequestParam(defaultValue = "true") boolean reset) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flightService.generateFlightSchedule(eventId, reset));
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping("/{eventId}/flights")
    public ResponseEntity<List<FlightResponseDto>> getEventFlights(@PathVariable Long eventId) {
        return ResponseEntity.ok(flightService.getByEvent(eventId));
    }
}
