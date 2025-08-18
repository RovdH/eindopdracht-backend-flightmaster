package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.List;

public class EventRequestDto {

    @NotNull(message = "Datum van het event is verplicht")
    private LocalDate date;

    @NotBlank(message = "Locatie is verplicht")
    private String location;

    @Positive(message = "Vliegtijd moet groter zijn dan 0")
    private double flightTime;

    @NotEmpty(message = "Minimaal één helikopter is vereist")
    private List<Long> helicopterIds;

    // Getters en setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getFlightTime() {
        return flightTime;
    }

    public void setFlightTime(double flightTime) {
        this.flightTime = flightTime;
    }

    public List<Long> getHelicopterIds() {
        return helicopterIds;
    }

    public void setHelicopterIds(List<Long> helicopterIds) {
        this.helicopterIds = helicopterIds;
    }
}
