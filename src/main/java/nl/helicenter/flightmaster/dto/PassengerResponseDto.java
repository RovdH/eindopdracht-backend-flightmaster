package nl.helicenter.flightmaster.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerResponseDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Double weightKg;

    private Long flightId;
    private Long userId;
}
