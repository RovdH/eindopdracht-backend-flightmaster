package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerUpdateDto {

    @Size(max = 60)
    private String firstName;

    @Size(max = 60)
    private String lastName;

    @Email
    @Size(max = 120)
    private String email;

    @DecimalMin("10.0")
    @DecimalMax("135.0")
    private Double weightKg;
}
