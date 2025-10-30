package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.security.JwtUtil;
import nl.helicenter.flightmaster.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwt;
    private final UserService userService;

    public AuthController(AuthenticationManager am, UserDetailsService uds, JwtUtil jwt, UserService us) {
        this.authenticationManager = am;
        this.userDetailsService = uds;
        this.jwt = jwt;
        this.userService = us;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Long>> register(@Valid @RequestBody UserRequestDto dto) {
        dto.setRole("USER");
        Long id = userService.registerUser(dto);
        return ResponseEntity.created(URI.create("/users/" + id)).body(Map.of("Gebruiker is geregistreerd met id", id));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        var user = userDetailsService.loadUserByUsername(req.email());
        return ResponseEntity.ok(new AuthResponse("Bearer", jwt.generateAccessToken(user), jwt.generateRefreshToken(user)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest req) {
        var username = jwt.extractUsername(req.refreshToken());
        if (!jwt.isRefreshToken(req.refreshToken())) return ResponseEntity.status(401).build();
        var user = userDetailsService.loadUserByUsername(username);
        return ResponseEntity.ok(new AuthResponse("Bearer", jwt.generateAccessToken(user), jwt.generateRefreshToken(user)));
    }

    public record AuthRequest(String email, String password) {
    }

    public record RefreshRequest(String refreshToken) {
    }

    public record AuthResponse(String tokenType, String accessToken, String refreshToken) {
    }
}
