package nl.helicenter.flightmaster.dto;

import java.time.LocalTime;

public class FlightResponseDto {

    private Long id;
    private String flightNumber;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long eventId;
    private String helicopterCallSign;
    private double fuelBefore;
    private double fuelAfter;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getHelicopterCallSign() { return helicopterCallSign; }
    public void setHelicopterCallSign(String helicopterCallSign) { this.helicopterCallSign = helicopterCallSign; }

    public double getFuelBefore() { return fuelBefore; }
    public void setFuelBefore(double fuelBefore) { this.fuelBefore = fuelBefore; }

    public double getFuelAfter() { return fuelAfter; }
    public void setFuelAfter(double fuelAfter) { this.fuelAfter = fuelAfter; }
}
