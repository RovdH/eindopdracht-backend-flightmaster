package nl.helicenter.flightmaster.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "helicopters")
public class Helicopter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String callSign;

    private String type;
    private int capacity;
    private double fuelCapacity;
    private double fuelUsage;
    private boolean available;

    public Helicopter() {
    }
}
