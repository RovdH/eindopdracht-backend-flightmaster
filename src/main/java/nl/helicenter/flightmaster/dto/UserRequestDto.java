package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequestDto {

    @NotBlank(message = "Email is verplicht")
    @Email(message = "Ongeldig e-mailadres")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    @Size(min = 8, max = 64, message = "Wachtwoord moet 8-64 tekens zijn")
    private String password;

    @NotBlank(message = "Rol is verplicht")
    @Pattern(regexp = "USER|ADMIN", message = "Rol moet USER of ADMIN zijn")
    private String role;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
