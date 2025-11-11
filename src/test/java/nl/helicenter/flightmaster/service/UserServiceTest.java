package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.dto.UserUpdateDto;
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

    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    UserPhotoService userPhotoService;
    @Mock
    FileUploadRepository fileUploadRepository;
    @InjectMocks
    UserService userService;

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
    void patch_userNotFound_throws() {
        when(userRepository.findById(123L)).thenReturn(Optional.empty());

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("nieuw@voorbeeld.nl");

        assertThatThrownBy(() -> userService.patch(123L, dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User 123 niet gevonden");
        verify(userRepository, never()).save(any());
    }

    @Test
    void patch_emailSame_noValidation_andSave() {
        User user = new User();
        user.setId(5L);
        user.setEmail("Barry@voorbeeld.nl");
        user.setPassword("HASH");
        user.setRole("USER");

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("Barry@voorbeeld.nl");

        User saved = userService.patch(5L, dto);

        assertThat(saved.getEmail()).isEqualTo("Barry@voorbeeld.nl");
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository).save(user);
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void patch_emailTaken_throws() {
        User user = new User();
        user.setId(6L);
        user.setEmail("Henk@oudemail.nl");
        when(userRepository.findById(6L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("Henk@nieuwemail.nl")).thenReturn(true);

        UserUpdateDto dto = new UserUpdateDto();
        dto.setEmail("Henk@nieuwemail.nl");

        assertThatThrownBy(() -> userService.patch(6L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mailadres is al in gebruik.");

        verify(userRepository, never()).save(any());
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void patch_password_encode_save() {
        User user = new User();
        user.setId(7L);
        user.setEmail("Henk@voorbeeld.nl");
        user.setPassword("OLDHASH");
        user.setRole("USER");

        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("ReteSterkWW2019!")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setPassword("ReteSterkWW2019!");

        User saved = userService.patch(7L, dto);

        assertThat(saved.getPassword()).isEqualTo("ENCODED");
        verify(passwordEncoder).encode("ReteSterkWW2019!");
        verify(userRepository).save(user);
    }

    @Test
    void patch_passwordBlank() {
        User user = new User();
        user.setId(8L);
        user.setEmail("Freek@voorbeeld.nl");
        user.setPassword("OLDHASH");

        when(userRepository.findById(8L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setPassword("   ");

        User saved = userService.patch(8L, dto);

        assertThat(saved.getPassword()).isEqualTo("OLDHASH");
        verify(passwordEncoder, never()).encode(any());
        verify(userRepository).save(user);
    }

    @Test
    void patch_roleProvided_updatesRole() {
        User user = new User();
        user.setId(9L);
        user.setEmail("Freek@voorbeeld.nl");
        user.setPassword("HASH");
        user.setRole("USER");

        when(userRepository.findById(9L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserUpdateDto dto = new UserUpdateDto();
        dto.setRole("ADMIN");

        User saved = userService.patch(9L, dto);

        assertThat(saved.getRole()).isEqualTo("ADMIN");
        verify(userRepository).save(user);
    }

    @Test
    void assignPhotoToUser_userNotFound() {
        when(userRepository.findById(42L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.assignPhotoToUser("foto.png", 42L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User 42 niet gevonden");
        verify(fileUploadRepository, never()).save(any());
        verify(userRepository, never()).save(any());
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
