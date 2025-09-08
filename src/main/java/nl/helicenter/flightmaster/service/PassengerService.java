package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.PassengerRequestDto;
import nl.helicenter.flightmaster.dto.PassengerResponseDto;
import nl.helicenter.flightmaster.model.Flight;
import nl.helicenter.flightmaster.model.Passenger;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.repository.FlightRepository;
import nl.helicenter.flightmaster.repository.PassengerRepository;
import nl.helicenter.flightmaster.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
public class PassengerService {

    private final PassengerRepository passengerRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    public PassengerService(PassengerRepository passengerRepository,
                            FlightRepository flightRepository,
                            UserRepository userRepository) {
        this.passengerRepository = passengerRepository;
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PassengerResponseDto create(PassengerRequestDto dto) {

        if (dto == null) throw new IllegalArgumentException("Request body is empty");
        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new EntityNotFoundException("Flight " + dto.getFlightId() + " not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User " + dto.getUserId() + " not found"));

        long currentPassengers = passengerRepository.countByFlight_Id(flight.getId());
        int capacity = flight.getHelicopter().getCapacity();

        if (currentPassengers >= capacity) {
            throw new IllegalStateException("Flight is full");
        }

        Passenger passenger = mapToEntity(dto, flight, user);
        Passenger savedPassenger = passengerRepository.save(passenger);

        PassengerResponseDto responseDto = new PassengerResponseDto();
        responseDto.setId(savedPassenger.getId());
        responseDto.setFirstName(savedPassenger.getFirstName());
        responseDto.setLastName(savedPassenger.getLastName());
        responseDto.setEmail(savedPassenger.getEmail());
        responseDto.setWeightKg(savedPassenger.getWeight());
        responseDto.setFlightId(flight.getId());
        responseDto.setUserId(user.getId());

        return responseDto;
    }

    @Transactional
    public List<PassengerResponseDto> createBulk(List<PassengerRequestDto> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Request list is empty");
        }

        Map<Long, List<PassengerRequestDto>> byFlight = requests.stream()
                .collect(Collectors.groupingBy(PassengerRequestDto::getFlightId));

        Map<Long, Flight> flights = flightRepository.findAllById(byFlight.keySet())
                .stream().collect(Collectors.toMap(Flight::getId, f -> f));

        for (Map.Entry<Long, List<PassengerRequestDto>> entry : byFlight.entrySet()) {
            Long flightId = entry.getKey();
            Flight flight = flights.get(flightId);
            if (flight == null) {
                throw new EntityNotFoundException("Flight " + flightId + " not found");
            }
            long current = passengerRepository.countByFlight_Id(flightId);
            int capacity = flight.getHelicopter().getCapacity();
            int incoming = entry.getValue().size();
            if (current + incoming > capacity) {
                throw new IllegalStateException("Flight " + flightId + " is full: capacity=" + capacity +
                        ", alreadyBooked=" + current + ", requested=" + incoming);
            }
        }

        Set<Long> userIds = requests.stream().map(PassengerRequestDto::getUserId).collect(Collectors.toSet());
        Map<Long, User> users = userRepository.findAllById(userIds)
                .stream().collect(Collectors.toMap(User::getId, u -> u));

        List<Passenger> toSave = new ArrayList<>();
        for (PassengerRequestDto dto : requests) {
            Flight flight = flights.get(dto.getFlightId());
            User user = users.get(dto.getUserId());
            if (user == null) {
                throw new EntityNotFoundException("User " + dto.getUserId() + " not found");
            }
            Passenger passenger = mapToEntity(dto, flight, user);
            toSave.add(passenger);
        }

        List<Passenger> saved = passengerRepository.saveAll(toSave);
        return saved.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PassengerResponseDto getById(Long passengerId) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() -> new EntityNotFoundException("Passenger " + passengerId + " not found"));
        return toResponse(passenger);
    }

    @Transactional
    public void delete(Long passengerId) {
        if (!passengerRepository.existsById(passengerId)) {
            throw new EntityNotFoundException("Passenger " + passengerId + " not found");
        }
        passengerRepository.deleteById(passengerId);
    }


    @Transactional(readOnly = true)
    public List<PassengerResponseDto> listByFlight(Long flightId) {
        List<Passenger> passengers = passengerRepository.findAllByFlight_Id(flightId);
        return passengers.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<PassengerResponseDto> listByUser(Long userId) {
        List<Passenger> passengers = passengerRepository.findAllByUser_Id(userId);
        return passengers.stream().map(this::toResponse).toList();
    }

    private PassengerResponseDto toResponse(Passenger passenger) {
        PassengerResponseDto dto = new PassengerResponseDto();
        dto.setId(passenger.getId());
        dto.setFirstName(passenger.getFirstName());
        dto.setLastName(passenger.getLastName());
        dto.setEmail(passenger.getEmail());
        dto.setWeightKg(passenger.getWeight());
        dto.setFlightId(passenger.getFlight().getId());
        dto.setUserId(passenger.getUser().getId());
        return dto;
    }
    //Mapper om niet 2x dit lijstje op te roepen in de create 1 en create bulk
    private Passenger mapToEntity(PassengerRequestDto dto, Flight flight, User user) {
        Passenger passenger = new Passenger();
        passenger.setFirstName(dto.getFirstName());
        passenger.setLastName(dto.getLastName());
        passenger.setEmail(dto.getEmail());
        passenger.setWeight(dto.getWeightKg());
        passenger.setFlight(flight);
        passenger.setUser(user);
        return passenger;
    }


}
