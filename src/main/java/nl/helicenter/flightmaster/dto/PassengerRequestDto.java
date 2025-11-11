package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerRequestDto {

    @NotBlank
    @Size(max = 60)
    private String firstName;

    @NotBlank
    @Size(max = 60)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 120)
    private String email;

    @NotNull
    @DecimalMin("10.0")
    @DecimalMax("135.0")
    private Double weightKg;

    @NotNull
    private Long flightId;

    @NotNull
    private Long userId;
}
