package nl.helicenter.flightmaster.repository;

import nl.helicenter.flightmaster.model.Helicopter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HelicopterRepository extends JpaRepository<Helicopter, Long> {
}
