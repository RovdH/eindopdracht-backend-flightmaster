package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class HelicopterRequestDto {

    @NotBlank(message = "Callsign is verplicht en mag niet leeg zijn")
    private String callSign;

    @NotBlank(message = "Type is verplicht")
    private String type;

    @Positive(message = "Aantal zitplaatsen moet groter zijn dan 0")
    private int capacity;

    @Positive(message = "Brandstoftankinhoud moet groter zijn dan 0")
    private double fuelCapacity;

    @Positive(message = "Brandstofverbruik moet groter zijn dan 0")
    private double fuelUsage;

    @NotNull(message = "Beschikbaarheidsstatus is verplicht")
    private Boolean available;

    public String getCallSign() {
        return callSign;
    }

    public void setCallSign(String callSign) {
        this.callSign = callSign;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public double getFuelCapacity() {
        return fuelCapacity;
    }

    public void setFuelCapacity(double fuelCapacity) {
        this.fuelCapacity = fuelCapacity;
    }

    public double getFuelUsage() {
        return fuelUsage;
    }

    public void setFuelUsage(double fuelUsage) {
        this.fuelUsage = fuelUsage;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }
}
