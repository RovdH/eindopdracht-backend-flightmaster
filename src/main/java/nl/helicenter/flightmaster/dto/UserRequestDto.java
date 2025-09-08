package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class UserRequestDto {

    @NotBlank(message = "Email is verplicht")
    @Email(message = "Ongeldig e-mailadres")
    private String email;

    @NotBlank(message = "Wachtwoord is verplicht")
    private String password;

    @NotBlank(message = "Rol is verplicht")
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
