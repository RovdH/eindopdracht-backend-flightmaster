package nl.helicenter.flightmaster.dto;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
public class EventUpdateDto {

    @FutureOrPresent(message = "Datum mag niet in het verleden liggen")
    private LocalDate eventDate;

    private String location;

    @Positive(message = "Vliegtijd moet groter zijn dan 0")
    private Double flightTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private List<@Positive Long> helicopterIds;

    @AssertTrue(message = "Eindtijd moet later zijn dan starttijd")
    public boolean isValidTimeWindow() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }
}