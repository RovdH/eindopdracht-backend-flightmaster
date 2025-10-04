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
import nl.helicenter.flightmaster.repository.PassengerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FlightService {

    private static final double FUEL_RESERVE = 30.0; // liters
    private static final int REFUEL_MINUTES = 30;    // minutes

    private final FlightRepository flightRepository;
    private final EventRepository eventRepository;
    private final HelicopterRepository helicopterRepository;
    private final PassengerRepository passengerRepository;

    public FlightService(FlightRepository flightRepository,
                         EventRepository eventRepository,
                         HelicopterRepository helicopterRepository, PassengerRepository passengerRepository) {
        this.flightRepository = flightRepository;
        this.eventRepository = eventRepository;
        this.helicopterRepository = helicopterRepository;
        this.passengerRepository = passengerRepository;
    }
    
    public FlightResponseDto create(FlightRequestDto dto) {
        Event event = eventRepository.findById(dto.getEventId())
                .orElseThrow(() -> new EntityNotFoundException("Event " + dto.getEventId() + " niet gevonden."));
        Helicopter heli = helicopterRepository.findById(dto.getHelicopterId())
                .orElseThrow(() -> new EntityNotFoundException("Helicopter " + dto.getHelicopterId() + " niet gevonden."));

        if (!event.getHelicopters().contains(heli)) {
            throw new IllegalArgumentException("Helicopter " + heli.getCallSign() + " is niet toegewezen aan event " + event.getId());
        }

        return generateFlightSlot(event, heli)
                .map(this::mapToResponse)
                .orElseThrow(() -> new IllegalArgumentException("Geen ruimte meer binnen het eventvenster voor een extra vlucht."));
    }

    public List<FlightResponseDto> generateFlightSchedule(Long eventId, boolean reset) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event " + eventId + " niet gevonden."));
        if (event.getHelicopters() == null || event.getHelicopters().isEmpty()) {
            throw new IllegalArgumentException("Geen helikopters toegewezen aan event " + eventId);
        }
        if (reset) {
            flightRepository.deleteByEvent_Id(eventId);
        }

        List<FlightResponseDto> created = new java.util.ArrayList<>();
        for (Helicopter heli : event.getHelicopters()) {
            while (true) {
                Optional<Flight> planned = generateFlightSlot(event, heli);
                if (planned.isEmpty()) break;
                created.add(mapToResponse(planned.get()));
            }
        }

        created.sort(Comparator.comparing(FlightResponseDto::getStartTime)
                .thenComparing(FlightResponseDto::getHelicopterCallSign));
        return created;
    }

    public FlightResponseDto getById(Long id) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flight " + id + " niet gevonden."));
        return mapToResponse(flight);
    }

    public List<FlightResponseDto> getByEvent(Long eventId) {
        return flightRepository.findByEvent_Id(eventId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<FlightResponseDto> getAll() {
        return flightRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Optional<Flight> generateFlightSlot(Event event, Helicopter heli) {

        Optional<Flight> prevFlightOpt =
                flightRepository.findTopByEvent_IdAndHelicopter_IdOrderByStartTimeDesc(event.getId(), heli.getId());

        LocalTime start = prevFlightOpt
                .map(flight -> flight.getStartTime().plusMinutes((long) event.getFlightTime()))
                .orElse(event.getStartTime());

        double fuelBefore = prevFlightOpt
                .map(Flight::getFuelAfter)
                .orElse(heli.getFuelCapacity());


        double minutes = event.getFlightTime();
        double consumptionPerFlight = (heli.getFuelUsage() / 60.0) * minutes;


        if (fuelBefore - consumptionPerFlight < FUEL_RESERVE) {
            start = start.plusMinutes(REFUEL_MINUTES);
            fuelBefore = heli.getFuelCapacity();
        }
        
        LocalTime calcEndTime = start.plusMinutes((long) minutes);
        if (start.isBefore(event.getStartTime()) || calcEndTime.isAfter(event.getEndTime())) {
            return Optional.empty();
        }
        
        long idx = flightRepository.countByEvent_Id(event.getId()) + 1;
        String flightNumber = "FL" + idx;
        
        Flight flight = new Flight();
        flight.setEvent(event);
        flight.setHelicopter(heli);
        flight.setFlightNumber(flightNumber);
        flight.setStartTime(start);
        flight.setFuelBefore(fuelBefore);
        flight.setFuelAfter(fuelBefore - consumptionPerFlight);

        return Optional.of(flightRepository.save(flight));
    }

    @Transactional
    public void delete(Long id) {
        if (!flightRepository.existsById(id)) {
            throw new EntityNotFoundException("Flight with id" + id + " not found");
        }
        flightRepository.deleteById(id);
    }

    private FlightResponseDto mapToResponse(Flight flight) {
        int capacityTotal = flight.getHelicopter().getCapacity();
        long seatsBooked = passengerRepository.countByFlight_Id(flight.getId());
        int seatsAvailable = Math.max(0, capacityTotal - (int) seatsBooked);

        FlightResponseDto dto = new FlightResponseDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setStartTime(flight.getStartTime());
        dto.setEndTime(flight.getStartTime().plusMinutes((long) flight.getEvent().getFlightTime()));
        dto.setEventId(flight.getEvent().getId());

        dto.setCapacityTotal(capacityTotal);
        dto.setSeatsBooked(seatsBooked);
        dto.setSeatsAvailable(seatsAvailable);

        dto.setHelicopterCallSign(flight.getHelicopter().getCallSign());
        dto.setFuelBefore(flight.getFuelBefore());
        dto.setFuelAfter(flight.getFuelAfter());
        return dto;
    }


    public List<FlightResponseDto> listByEvent(Long eventId) {
        List<Flight> flights = flightRepository.findByEvent_Id(eventId);
        return flights.stream().map(flight -> {
            int capacityTotal = flight.getHelicopter().getCapacity();
            long booked = passengerRepository.countByFlight_Id(flight.getId());
            int seatsAvailable = Math.max(0, capacityTotal - (int) booked );

            FlightResponseDto dto = new FlightResponseDto();
            dto.setId(flight.getId());
            dto.setFlightNumber(flight.getFlightNumber());
            dto.setStartTime(flight.getStartTime());
            dto.setCapacityTotal(capacityTotal);
            dto.setSeatsBooked(booked);
            dto.setSeatsAvailable(seatsAvailable);
            dto.setHelicopterCallSign(flight.getHelicopter().getCallSign());
            return dto;
        }).toList();
    }
}
