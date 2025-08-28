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
        passenger.setWeightKg(dto.getWeightKg());
        passenger.setFlight(flight);
        passenger.setUser(user);

        Passenger savedPassenger = passengerRepository.save(passenger);

        PassengerResponseDto responseDto = new PassengerResponseDto();
        responseDto.setId(savedPassenger.getId());
        responseDto.setFirstName(savedPassenger.getFirstName());
        responseDto.setLastName(savedPassenger.getLastName());
        responseDto.setEmail(savedPassenger.getEmail());
        responseDto.setWeightKg(savedPassenger.getWeightKg());
        responseDto.setFlightId(flight.getId());
        responseDto.setUserId(user.getId());

        return responseDto;
    }
}
