package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.model.UserPhoto;
import nl.helicenter.flightmaster.repository.FileUploadRepository;
import nl.helicenter.flightmaster.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock UserPhotoService userPhotoService;
    @Mock FileUploadRepository fileUploadRepository;
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
    void registerUser_emailAlreadyExists() {
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

    @Test
    void assignInitialPhotoToUser() {
        Long userId = 1L;
        String fileName = "test.jpg";

        User user = new User();
        user.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileUploadRepository.findById(fileName)).thenReturn(Optional.empty());
        when(fileUploadRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.assignPhotoToUser(fileName, userId);

        assertThat(result.getUserPhoto()).isNotNull();
        assertThat(result.getUserPhoto().getFileName()).isEqualTo(fileName);
        verify(fileUploadRepository).save(any());
        verify(userRepository).save(user);
    }

    @Test
    void assignPhotoToUser_ExistingPhoto() {
        Long userId = 1L;
        String fileName = "test.jpg";

        User user = new User();
        user.setId(userId);

        UserPhoto existing = new UserPhoto(fileName);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(fileUploadRepository.findById(fileName)).thenReturn(Optional.of(existing));
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        User result = userService.assignPhotoToUser(fileName, userId);

        assertThat(result.getUserPhoto()).isSameAs(existing);
        verify(fileUploadRepository, never()).save(any());
        verify(userRepository).save(user);
    }

    @Test
    void getPhotoFromUser_userNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getPhotoFromUser(99L));
    }

    @Test
    void getPhotoFromUser_noPhoto() {
        User user = new User();
        user.setId(10L);
        when(userRepository.findById(10L)).thenReturn(Optional.of(user));
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.getPhotoFromUser(10L));
    }

    @Test
    void getPhotoFromUser_returnsResource() {
        Long id = 1L;
        String fileName = "test.png";
        UserPhoto up = new UserPhoto(fileName);
        User u = new User();
        u.setId(id);
        u.setUserPhoto(up);

      Resource mockRes = mock(Resource.class);

        when(userRepository.findById(id)).thenReturn(Optional.of(u));
        when(userPhotoService.downLoadFile(fileName)).thenReturn(mockRes);

        Resource res = userService.getPhotoFromUser(id);
        assertThat(res).isSameAs(mockRes);
        verify(userPhotoService).downLoadFile(fileName);
    }

    @Test
    void deletePhotoFromUser_noPhoto_noop() {
        User user = new User();
        user.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));

        userService.deletePhotoFromUser(2L);

        verify(userRepository, never()).save(any());
        verify(userPhotoService, never()).deleteFile(any());
    }

    @Test
    void deletePhotoFromUser_withPhoto_deletesAndClears() {
        User user = new User();
        user.setId(13L);
        user.setUserPhoto(new UserPhoto("delete.png"));

        when(userRepository.findById(13L)).thenReturn(Optional.of(user));

        userService.deletePhotoFromUser(13L);

        assertThat(user.getUserPhoto()).isNull();
        verify(userRepository).save(user);
        verify(userPhotoService).deleteFile("delete.png");
    }

    @Test
    void delete_userDoesNotExist_throws() {
        when(userRepository.existsById(88L)).thenReturn(false);
        Assertions.assertThrows(EntityNotFoundException.class,
                () -> userService.delete(88L));
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void delete_userExists() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.delete(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void getAllUsers_returnsRepoResult() {
        List<User> list = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(list);
        assertThat(userService.getAllUsers()).hasSize(2);
        verify(userRepository).findAll();
    }
}
