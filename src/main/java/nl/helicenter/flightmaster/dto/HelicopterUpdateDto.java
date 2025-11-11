package nl.helicenter.flightmaster.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HelicopterUpdateDto {
    private String callSign;
    private int capacity;
    private double fuelUsage;
    private double fuelCapacity;
    private Boolean available;
    private String type;
}
