package nl.helicenter.flightmaster.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key key;
    private final long accessMinutes;
    private final long refreshDays;

    public JwtUtil(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-minutes:15}") long accessMinutes,
            @Value("${app.jwt.refresh-days:30}") long refreshDays
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessMinutes = accessMinutes;
        this.refreshDays = refreshDays;
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean validateToken(String token, UserDetails user) {
        return user.getUsername().equals(extractUsername(token)) && !isExpired(token);
    }

    public String generateAccessToken(UserDetails user) {
        long exp = System.currentTimeMillis() + accessMinutes * 60_000;
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(UserDetails user) {
        long exp = System.currentTimeMillis() + refreshDays * 24L * 60L * 60L * 1000L;
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("type","refresh")
                .setIssuedAt(new Date())
                .setExpiration(new Date(exp))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isRefreshToken(String token) {
        Object t = claims(token).get("type");
        return "refresh".equals(t);
    }

    private boolean isExpired(String token) {
        return claims(token).getExpiration().before(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    }
}
