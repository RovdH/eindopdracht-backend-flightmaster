package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class FlightRequestDto {

    @NotNull(message = "EventId is verplicht")
    private Long eventId;

    @NotNull(message = "HelicopterId is verplicht")
    private Long helicopterId;

    @NotBlank(message = "Vluchtnummer is verplicht (bijv. FL1)")
    private String flightNumber;

    @NotNull(message = "Starttijd is verplicht")
    private LocalTime startTime;

    // Nu ga ik het nog handmatig toevoegen dus is dit handig. Als de business logic wordt toegepast dan gaat dit waarschijnlijk niet hier gebeuren.
    private Double fuelBefore;
    private Double fuelAfter;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getHelicopterId() { return helicopterId; }
    public void setHelicopterId(Long helicopterId) { this.helicopterId = helicopterId; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public Double getFuelBefore() { return fuelBefore; }
    public void setFuelBefore(Double fuelBefore) { this.fuelBefore = fuelBefore; }

    public Double getFuelAfter() { return fuelAfter; }
    public void setFuelAfter(Double fuelAfter) { this.fuelAfter = fuelAfter; }
}
