package nl.helicenter.flightmaster.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventResponseDto {

    private Long id;
    private LocalDate eventDate;
    private String location;
    private double flightTime;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> helicopterCallSigns;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
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

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public List<String> getHelicopterCallSigns() {
        return helicopterCallSigns;
    }

    public void setHelicopterCallSigns(List<String> helicopterCallSigns) {
        this.helicopterCallSigns = helicopterCallSigns;
    }
}
