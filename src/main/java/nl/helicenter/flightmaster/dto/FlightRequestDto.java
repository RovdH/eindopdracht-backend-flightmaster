package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class FlightRequestDto {

    @NotNull(message = "EventId is verplicht")
    private Long eventId;

    @NotNull(message = "HelicopterId is verplicht")
    private Long helicopterId;

    @NotBlank(message = "Vluchtnummer is verplicht (bijv. FL1)")
    private String flightNumber;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getHelicopterId() { return helicopterId; }
    public void setHelicopterId(Long helicopterId) {
        this.helicopterId = helicopterId;
    }
}