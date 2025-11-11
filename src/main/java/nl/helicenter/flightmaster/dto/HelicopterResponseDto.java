package nl.helicenter.flightmaster.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HelicopterResponseDto {
    private Long id;
    private String callSign;
    private String type;
    private int capacity;
    private double fuelCapacity;
    private double fuelUsage;
    private boolean available;
}
