package nl.helicenter.flightmaster.repository;

import nl.helicenter.flightmaster.model.Flight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByEvent_Id(Long eventId);
    Optional<Flight> findTopByEvent_IdAndHelicopter_IdOrderByStartTimeDesc(Long eventId, Long helicopterId);
    long countByEvent_Id(Long eventId);
    void deleteByEvent_Id(Long eventId);
}
