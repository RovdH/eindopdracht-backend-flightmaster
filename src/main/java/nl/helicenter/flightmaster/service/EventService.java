package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.EventRequestDto;
import nl.helicenter.flightmaster.dto.EventResponseDto;
import nl.helicenter.flightmaster.model.Event;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.repository.EventRepository;
import nl.helicenter.flightmaster.repository.HelicopterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final HelicopterRepository helicopterRepository;

    public EventService(EventRepository eventRepository, HelicopterRepository helicopterRepository) {
        this.eventRepository = eventRepository;
        this.helicopterRepository = helicopterRepository;
    }

    public EventResponseDto createEvent(EventRequestDto dto) {
        List<Helicopter> helicopters = helicopterRepository.findAllById(dto.getHelicopterIds());

         if (helicopters.size() != dto.getHelicopterIds().size()) {
            throw new EntityNotFoundException("Een of meer helikopters niet gevonden.");
        }

         for (Helicopter heli : helicopters) {
            boolean conflict = eventRepository.existsByDateAndHelicopters_Id(dto.getDate(), heli.getId());
            if (conflict) {
                throw new IllegalArgumentException("Helicopter " + heli.getCallSign() + " is al geboekt op " + dto.getDate());
            }
        }

        Event event = new Event();
        event.setDate(dto.getDate());
        event.setLocation(dto.getLocation());
        event.setFlightTime(dto.getFlightTime());
        event.setHelicopters(helicopters);

        Event saved = eventRepository.save(event);
        return mapToResponseDto(saved);
    }

    public EventResponseDto getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Event met id " + id + " niet gevonden."));
        return mapToResponseDto(event);
    }

    public List<EventResponseDto> getAll() {
        return eventRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private EventResponseDto mapToResponseDto(Event event) {
        EventResponseDto dto = new EventResponseDto();
        dto.setId(event.getId());
        dto.setDate(event.getDate());
        dto.setLocation(event.getLocation());
        dto.setFlightTime(event.getFlightTime());
        dto.setHelicopterCallSigns(
                event.getHelicopters().stream()
                        .map(Helicopter::getCallSign)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}
