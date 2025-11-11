package nl.helicenter.flightmaster.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(
        name = "flights",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_flight_event_flightnumber", columnNames = {"event_id", "flight_number"}),
        }
)
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false)
    private String flightNumber;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(optional = false)
    @JoinColumn(name = "helicopter_id", nullable = false)
    private Helicopter helicopter;

    @Column(name = "fuel_before", nullable = false)
    private double fuelBefore;

    @Column(name = "fuel_after", nullable = false)
    private double fuelAfter;

    public Flight() {
    }
}
