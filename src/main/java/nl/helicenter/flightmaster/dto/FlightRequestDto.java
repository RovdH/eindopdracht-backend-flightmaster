package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class FlightRequestDto {

    @NotNull(message = "EventId is verplicht")
    @Positive(message = "EventId moet > 0 zijn")
    private Long eventId;

    @NotNull(message = "HelicopterId is verplicht")
    @Positive(message = "HelicopterId moet > 0 zijn")
    private Long helicopterId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getHelicopterId() {
        return helicopterId;
    }

    public void setHelicopterId(Long helicopterId) {
        this.helicopterId = helicopterId;
    }
}