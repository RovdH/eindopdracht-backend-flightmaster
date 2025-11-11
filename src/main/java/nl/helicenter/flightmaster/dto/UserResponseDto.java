package nl.helicenter.flightmaster.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String email;
    private String role;

    public UserResponseDto() {
    }

    public UserResponseDto(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }
}
