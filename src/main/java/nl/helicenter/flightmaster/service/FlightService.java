package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.FlightRequestDto;
import nl.helicenter.flightmaster.dto.FlightResponseDto;
import nl.helicenter.flightmaster.model.Event;
import nl.helicenter.flightmaster.model.Flight;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.repository.EventRepository;
import nl.helicenter.flightmaster.repository.FlightRepository;
import nl.helicenter.flightmaster.repository.HelicopterRepository;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private static final double FUEL_RESERVE = 30.0;
    private static final int REFUEL_MINUTES = 30;

    private final FlightRepository flightRepository;
    private final EventRepository eventRepository;
    private final HelicopterRepository helicopterRepository;

    public FlightService(FlightRepository flightRepository,
                         EventRepository eventRepository,
                         HelicopterRepository helicopterRepository) {
        this.flightRepository = flightRepository;
        this.eventRepository = eventRepository;
        this.helicopterRepository = helicopterRepository;
    }

    public FlightResponseDto create(FlightRequestDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event " + dto.getEventId() + " niet gevonden."));
        Helicopter heli = helicopterRepository.findById(dto.getHelicopterId())
                .orElseThrow(() -> new EntityNotFoundException("Helicopter " + dto.getHelicopterId() + " niet gevonden."));

        if (!event.getHelicopters().contains(heli)) {
            throw new IllegalArgumentException("Helicopter " + heli.getCallSign() + " is niet toegewezen aan event " + event.getId());
        }

        LocalTime start;
        double fuelBefore;
        double fuelAfter;

        Optional<Flight> prevFlightOpt =
                flightRepository.findTopByEvent_IdAndHelicopter_IdOrderByStartTimeDesc(event.getId(), heli.getId());

        if (prevFlightOpt.isEmpty()) {
              start = event.getStartTime();
            fuelBefore = heli.getFuelCapacity();
        } else {
            Flight prevFlight = prevFlightOpt.get();
            start = prevFlight.getStartTime().plusMinutes((long) event.getFlightTime());
            fuelBefore = prevFlight.getFuelAfter();

            double consumptionPerFlight = event.getFlightTime() * heli.getFuelUsage();

            if (fuelBefore - consumptionPerFlight < FUEL_RESERVE) {
                start = start.plusMinutes(REFUEL_MINUTES);
                fuelBefore = heli.getFuelCapacity();
            }
        }

        LocalTime endCandidate = start.plusMinutes((long) event.getFlightTime());
        if (start.isBefore(event.getStartTime()) || endCandidate.isAfter(event.getEndTime())) {
            throw new IllegalArgumentException("Geen ruimte meer binnen het eventvenster voor een extra vlucht.");
        }

        double consumptionPerFlight = event.getFlightTime() * heli.getFuelUsage();
        fuelAfter = fuelBefore - consumptionPerFlight;

        long idx = flightRepository.countByEvent_Id(event.getId()) + 1;
        String flightNumber = "FL" + idx;

        Flight f = new Flight();
        f.setEvent(event);
        f.setHelicopter(heli);
        f.setFlightNumber(flightNumber);
        f.setStartTime(start);
        f.setFuelBefore(fuelBefore);
        f.setFuelAfter(fuelAfter);

        Flight saved = flightRepository.save(f);
        return mapToResponse(saved);
    }

    public FlightResponseDto getById(Long id) {
        Flight f = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight " + id + " niet gevonden."));
        return mapToResponse(f);
    }

    public List<FlightResponseDto> getByEvent(Long eventId) {
        return flightRepository.findByEvent_Id(eventId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FlightResponseDto> getAll() {
        return flightRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private FlightResponseDto mapToResponse(Flight f) {
        FlightResponseDto dto = new FlightResponseDto();
        dto.setId(f.getId());
        dto.setFlightNumber(f.getFlightNumber());
        dto.setStartTime(f.getStartTime());
        dto.setEndTime(f.getStartTime().plusMinutes((long) f.getEvent().getFlightTime()));
        dto.setEventId(f.getEvent().getId());
        dto.setHelicopterCallSign(f.getHelicopter().getCallSign());
        dto.setFuelBefore(f.getFuelBefore());
        dto.setFuelAfter(f.getFuelAfter());
        return dto;
    }
}
