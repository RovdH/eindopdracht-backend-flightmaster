package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HelicopterRequestDto {

    @NotBlank(message = "Callsign is verplicht en mag niet leeg zijn")
    @Size(max = 6, message = "Callsign mag max 6 tekens zijn")
    private String callSign;

    @NotBlank(message = "Helicopter Type is verplicht")
    private String type;

    @Min(value = 2, message = "Minimaal 2 zitplaatsen")
    @Max(value = 8, message = "Maximaal 8 zitplaatsen")
    @Positive(message = "Aantal zitplaatsen moet groter zijn dan 0")
    private int capacity;

    @Positive(message = "Brandstoftankinhoud moet groter zijn dan 0")
    private double fuelCapacity;

    @Positive(message = "Brandstofverbruik moet groter zijn dan 0")
    private double fuelUsage;

    @NotNull(message = "Beschikbaarheidsstatus is verplicht")
    private Boolean available;

}
