package nl.helicenter.flightmaster.dto;

import jakarta.validation.constraints.*;

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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }
}
