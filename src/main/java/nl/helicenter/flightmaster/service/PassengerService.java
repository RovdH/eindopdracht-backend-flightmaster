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
        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new EntityNotFoundException("Flight " + dto.getFlightId() + " not found"));

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User " + dto.getUserId() + " not found"));

        long currentPassengers = passengerRepository.countByFlight_Id(flight.getId());
        int capacity = flight.getHelicopter().getCapacity();

        if (currentPassengers >= capacity) {
            throw new IllegalStateException("Flight is full");
        }

        Passenger passenger = new Passenger();
        passenger.setFirstName(dto.getFirstName());
        passenger.setLastName(dto.getLastName());
        passenger.setEmail(dto.getEmail());
        passenger.setWeight(dto.getWeightKg());
        passenger.setFlight(flight);
        passenger.setUserId(user);

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
        dto.setUserId(passenger.getUserId().getId());
        return dto;
    }


}
