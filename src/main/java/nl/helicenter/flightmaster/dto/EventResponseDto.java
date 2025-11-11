package nl.helicenter.flightmaster.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class EventResponseDto {
    private Long id;
    private LocalDate eventDate;
    private String location;
    private double flightTime;
    private LocalTime startTime;
    private LocalTime endTime;
    private List<String> helicopterCallSigns;
}
