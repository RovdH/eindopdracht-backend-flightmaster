package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.*;

public class HelicopterRequestDto {

    @NotBlank(message = "Callsign is verplicht en mag niet leeg zijn")
    @Size(max = 6, message = "Callsign mag max 6 tekens zijn")
    private String callSign;

    @NotBlank(message = "Helicopter Type is verplicht")
    private String type;

    @Min(value = 2, message = "Minimaal 2 zitplaatsen")
    @Max(value = 8, message = "Maximaal 8 zitplaatsen")
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
