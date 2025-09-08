package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserRequestDto dto) {
        String email = userService.registerUser(dto);
        return ResponseEntity.ok("Gebruiker geregistreerd: " + email);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

}
