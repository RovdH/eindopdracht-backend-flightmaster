package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.*;
import nl.helicenter.flightmaster.model.Event;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.service.EventService;
import nl.helicenter.flightmaster.service.FlightService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/events")
@Validated
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

    @Transactional
    @PostMapping("/{id}/flights/generate")
    public ResponseEntity<List<FlightResponseDto>> generate(
            @PathVariable("id") @Positive Long eventId,
            @RequestParam(defaultValue = "true") boolean reset) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(flightService.generateFlightSchedule(eventId, reset));
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDto>> getAll() {
        return ResponseEntity.ok(eventService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDto> getById(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(eventService.getById(id));
    }

    @GetMapping("/{eventId}/flights")
    public ResponseEntity<List<FlightResponseDto>> getEventFlights(@PathVariable @Positive Long eventId) {
        return ResponseEntity.ok(flightService.getByEvent(eventId));
    }

    @PatchMapping("update/{id}")
    public ResponseEntity<EventResponseDto> patchEvent(
            @PathVariable Long id,
            @RequestBody @Valid EventUpdateDto dto) {
        eventService.patch(id, dto);
        return ResponseEntity.ok(eventService.getById(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        eventService.delete(id);
    }
}
