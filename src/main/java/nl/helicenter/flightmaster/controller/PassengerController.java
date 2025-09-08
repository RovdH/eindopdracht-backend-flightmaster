package nl.helicenter.flightmaster.controller;

import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.PassengerRequestDto;
import nl.helicenter.flightmaster.dto.PassengerResponseDto;
import nl.helicenter.flightmaster.service.PassengerService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@RestController
@RequestMapping("/passengers")
@Validated
public class PassengerController {

    private final PassengerService passengerService;

    public PassengerController(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    @PostMapping
    public ResponseEntity<PassengerResponseDto> create(
            @RequestBody @Valid PassengerRequestDto passengerRequestDto,
            UriComponentsBuilder uriBuilder) {

        PassengerResponseDto passengerResponseDto = passengerService.create(passengerRequestDto);

        return ResponseEntity
                .created(
                        uriBuilder
                                .path("/passengers/{id}")
                                .buildAndExpand(passengerResponseDto.getId())
                                .toUri()
                )
                .body(passengerResponseDto);
    }

    @PostMapping("/bulk")
    public ResponseEntity<List<PassengerResponseDto>> createBulk(@RequestBody @Valid List<@Valid PassengerRequestDto> passengerRequests) {
        List<PassengerResponseDto> result = passengerService.createBulk(passengerRequests);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PassengerResponseDto> getById(@PathVariable @Positive Long id) {
        PassengerResponseDto passengerResponseDto = passengerService.getById(id);
        return ResponseEntity.ok(passengerResponseDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        passengerService.delete(id);
    }

    @GetMapping("/by-flight/{flightId}")
    public ResponseEntity<List<PassengerResponseDto>> listByFlight(@PathVariable Long flightId) {
        List<PassengerResponseDto> passengers = passengerService.listByFlight(flightId);
        return ResponseEntity.ok(passengers);
    }

    @GetMapping("/by-user/{userId}")
    public ResponseEntity<List<PassengerResponseDto>> listByUser(@PathVariable Long userId) {
        List<PassengerResponseDto> passengers = passengerService.listByUser(userId);
        return ResponseEntity.ok(passengers);
    }
}
