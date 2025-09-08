package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class EventRequestDto {

    @NotNull(message = "Datum van het event is verplicht")
    @FutureOrPresent(message = "Datum mag niet in het verleden liggen")
    private LocalDate eventDate;

    @NotBlank(message = "Locatie is verplicht")
    private String location;

    @Positive(message = "Vliegtijd moet groter zijn dan 0")
    private double flightTime;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "Starttijd is verplicht en moet in HH:mm formaat zijn")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    @NotNull(message = "Eindtijd is verplicht en moet in HH:mm formaat zijn")
    private LocalTime endTime;

    @AssertTrue(message = "Eindtijd moet later zijn dan starttijd")
    public boolean isValidTimeWindow() {
        if (startTime == null || endTime == null) {
            return true;
        }
        return endTime.isAfter(startTime);
    }

    @NotEmpty(message = "Minimaal één helikopter is vereist")
    private List<@Positive Long> helicopterIds;

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

    public List<Long> getHelicopterIds() {
        return helicopterIds;
    }

    public void setHelicopterIds(List<Long> helicopterIds) {
        this.helicopterIds = helicopterIds;
    }

}
