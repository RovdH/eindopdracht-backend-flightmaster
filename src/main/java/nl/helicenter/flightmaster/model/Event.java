package nl.helicenter.flightmaster.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalTime startTime;
    private LocalTime endTime;
    private LocalDate eventDate;
    private String location;
    private double flightTime;

    @ManyToMany
    @JoinTable(
            name = "event_helicopters",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "helicopter_id")
    )
    private List<Helicopter> helicopters = new ArrayList<>();

    public Event() {
    }
}
