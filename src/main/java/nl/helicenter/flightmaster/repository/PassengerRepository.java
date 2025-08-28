package nl.helicenter.flightmaster.repository;
import nl.helicenter.flightmaster.model.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassengerRepository extends JpaRepository<Passenger, Long> {
    long countByFlight_Id(Long flightId);
}
