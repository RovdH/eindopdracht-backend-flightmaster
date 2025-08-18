package nl.helicenter.flightmaster.dto;

import java.time.LocalDate;
import java.util.List;

public class EventResponseDto {

    private Long id;
    private LocalDate date;
    private String location;
    private double flightTime;

    private List<String> helicopterCallSigns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public List<String> getHelicopterCallSigns() {
        return helicopterCallSigns;
    }

    public void setHelicopterCallSigns(List<String> helicopterCallSigns) {
        this.helicopterCallSigns = helicopterCallSigns;
    }
}
