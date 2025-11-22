package nl.helicenter.flightmaster.repository;

import nl.helicenter.flightmaster.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    long countByFlight_Id(Long flightId);
    List<Passenger> findAllByFlight_Id(Long flightId);
    List<Passenger> findAllByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
}
