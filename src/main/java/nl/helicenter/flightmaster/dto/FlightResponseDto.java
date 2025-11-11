package nl.helicenter.flightmaster.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
public class FlightResponseDto {
    private Long id;
    private String flightNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long eventId;
    private String helicopterCallSign;
    private double fuelBefore;
    private double fuelAfter;
    private int capacityTotal;
    private long seatsBooked;
    private int seatsAvailable;
}
