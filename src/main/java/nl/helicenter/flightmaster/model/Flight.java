package nl.helicenter.flightmaster.model;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
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

    public Flight() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Helicopter getHelicopter() {
        return helicopter;
    }

    public void setHelicopter(Helicopter helicopter) {
        this.helicopter = helicopter;
    }

    public double getFuelBefore() {
        return fuelBefore;
    }

    public void setFuelBefore(double fuelBefore) {
        this.fuelBefore = fuelBefore;
    }

    public double getFuelAfter() {
        return fuelAfter;
    }

    public void setFuelAfter(double fuelAfter) {
        this.fuelAfter = fuelAfter;
    }
}
