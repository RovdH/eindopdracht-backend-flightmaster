package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FlightRequestDto {

    @NotNull(message = "EventId is verplicht")
    @Positive(message = "EventId moet > 0 zijn")
    private Long eventId;

    @NotNull(message = "HelicopterId is verplicht")
    @Positive(message = "HelicopterId moet > 0 zijn")
    private Long helicopterId;
}