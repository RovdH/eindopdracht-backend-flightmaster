package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwt;

    public AuthController(AuthenticationManager am, UserDetailsService uds, JwtUtil jwt) {
        this.authenticationManager = am;
        this.userDetailsService = uds;
        this.jwt = jwt;
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

    public record AuthRequest(String email, String password) {}
    public record RefreshRequest(String refreshToken) {}
    public record AuthResponse(String tokenType, String accessToken, String refreshToken) {}
}
