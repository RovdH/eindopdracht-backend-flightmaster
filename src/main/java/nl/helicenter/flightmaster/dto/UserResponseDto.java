package nl.helicenter.flightmaster.dto;

public class UserResponseDto {
    private Long id;
    private String email;
    private String role;

    public UserResponseDto() {}

    public UserResponseDto(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
}
