package nl.helicenter.flightmaster.service;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @InjectMocks UserService userService;

    @Test
    void createUser_mapsEncryptsAndSaves() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("henk@voorbeeld.nl");
        dto.setPassword("Geheimpje123!");
        dto.setRole("USER");

        when(passwordEncoder.encode("Geheimpje123!")).thenReturn("BECRYPTED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User user = inv.getArgument(0);
            user.setId(1L);
            return user;
        });

        Long result = userService.registerUser(dto);

        assertThat(result).isEqualTo(1L);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertThat(saved.getEmail()).isEqualTo("henk@voorbeeld.nl");
        assertThat(saved.getPassword()).isEqualTo("BECRYPTED");
        assertThat(saved.getRole()).isEqualTo("USER");
        verify(passwordEncoder).encode("Geheimpje123!");
    }

    @Test
    void registerUser_emailAlreadyExists_throws() {
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("henk@voorbeeld.nl");
        dto.setPassword("Geheimpje123!");
        dto.setRole("USER");

        when(userRepository.existsByEmail("henk@voorbeeld.nl")).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mailadres is al in gebruik.");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }
}
