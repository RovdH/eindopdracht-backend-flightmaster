package nl.helicenter.flightmaster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    @Email(message = "Ongeldig e-mailadres")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 8, max = 64, message = "Wachtwoord moet 8-64 tekens zijn")
    private String password;

    private String role;
}
