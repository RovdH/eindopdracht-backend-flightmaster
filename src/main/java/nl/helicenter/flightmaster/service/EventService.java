package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.EventRequestDto;
import nl.helicenter.flightmaster.dto.EventResponseDto;
import nl.helicenter.flightmaster.model.Event;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.repository.EventRepository;
import nl.helicenter.flightmaster.repository.HelicopterRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;

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
            boolean conflict = eventRepository.existsByEventDateAndHelicopters_Id(dto.getEventDate(), heli.getId());
            if (conflict) {
                throw new IllegalArgumentException("Helicopter " + heli.getCallSign() + " is al geboekt op " + dto.getEventDate());
            }
        }

        Event event = new Event();
        event.setDate(dto.getEventDate());
        event.setLocation(dto.getLocation());
        event.setFlightTime(dto.getFlightTime());
        event.setStartTime(dto.getStartTime());
        event.setEndTime(dto.getEndTime());
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
        dto.setEventDate(event.getDate());
        dto.setLocation(event.getLocation());
        dto.setFlightTime(event.getFlightTime());
        dto.setStartTime(event.getStartTime());
        dto.setEndTime(event.getEndTime());
        dto.setHelicopterCallSigns(
                event.getHelicopters().stream()
                        .map(Helicopter::getCallSign)
                        .collect(Collectors.toList())
        );
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Event with id" + id + " not found");
        }
        eventRepository.deleteById(id);
    }
}
