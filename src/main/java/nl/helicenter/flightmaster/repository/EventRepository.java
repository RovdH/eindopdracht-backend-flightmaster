package nl.helicenter.flightmaster.repository;

import nl.helicenter.flightmaster.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByEventDateAndHelicopters_Id(LocalDate eventDate, Long helicopterId);
}
