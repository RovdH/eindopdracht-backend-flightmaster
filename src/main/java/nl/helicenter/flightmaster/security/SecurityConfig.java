package nl.helicenter.flightmaster.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.sql.DataSource;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final DataSource dataSource;
    private final JwtRequestFilter jwtRequestFilter;
    private final RestAuthenticationEntryPoint authEntryPoint;
    private final AccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(
            DataSource dataSource,
            JwtRequestFilter jwtRequestFilter,
            RestAuthenticationEntryPoint authEntryPoint,
            RestAccessDeniedHandler accessDeniedHandler
    ) {
        this.dataSource = dataSource;
        this.jwtRequestFilter = jwtRequestFilter;
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .jdbcAuthentication()
                .dataSource(dataSource)
                .passwordEncoder(passwordEncoder())
                .usersByUsernameQuery("""
                SELECT email AS username, password, TRUE AS enabled
                FROM users
                WHERE email = ?
            """)
                .authoritiesByUsernameQuery("""
                SELECT email AS username, CONCAT('ROLE_', UPPER(role)) AS authority
                FROM users
                WHERE email = ?
            """);
        return builder.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        JdbcUserDetailsManager mgr = new JdbcUserDetailsManager(dataSource);
        mgr.setUsersByUsernameQuery("""
            SELECT email AS username, password, TRUE AS enabled
            FROM users
            WHERE email = ?
        """);
        mgr.setAuthoritiesByUsernameQuery("""
            SELECT email AS username, CONCAT('ROLE_', UPPER(role)) AS authority
            FROM users
            WHERE email = ?
        """);
        return mgr;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Publiek
                        .requestMatchers("/auth/register", "/auth/login", "/auth/refresh", "/actuator/health").permitAll()
                        // Auth required
                        .requestMatchers(HttpMethod.GET, "/users", "/users/*").authenticated()
                        .requestMatchers(HttpMethod.POST, "/users/*/photo").authenticated()
                        .requestMatchers(HttpMethod.PUT,  "/users/*/photo").authenticated()
                        // Admin-rechten only
                        .requestMatchers(HttpMethod.POST,   "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasRole("ADMIN")
                        .anyRequest().denyAll()
                );

        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        var cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("http://localhost:3000"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
