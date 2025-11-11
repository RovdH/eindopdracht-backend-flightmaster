package nl.helicenter.flightmaster.service;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.dto.UserUpdateDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.Resource;
import nl.helicenter.flightmaster.model.UserPhoto;
import nl.helicenter.flightmaster.repository.FileUploadRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import static nl.helicenter.flightmaster.utils.PatchUtil.applyIfPresent;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadRepository fileUploadRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPhotoService userPhotoService;

    public UserService(UserRepository userRepository, FileUploadRepository fileUploadRepository, UserPhotoService userPhotoService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userPhotoService = userPhotoService;
        this.fileUploadRepository = fileUploadRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long registerUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mailadres is al in gebruik.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(dto.getRole());
        User saved = userRepository.save(user);
        return saved.getId();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User assignPhotoToUser(String fileName, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " niet gevonden"));

        UserPhoto photo = fileUploadRepository.findById(fileName)
                .orElseGet(() -> fileUploadRepository.save(new UserPhoto(fileName)));

        user.setUserPhoto(photo);
        return userRepository.save(user);
    }

    public Resource getPhotoFromUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " niet gevonden"));

        if (user.getUserPhoto() == null) {
            throw new EntityNotFoundException("User " + userId + " heeft geen foto");
        }
        return userPhotoService.downLoadFile(user.getUserPhoto().getFileName());
    }

    public void deletePhotoFromUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " niet gevonden"));

        if (user.getUserPhoto() == null) return;

        String fileName = user.getUserPhoto().getFileName();
        user.setUserPhoto(null);
        userRepository.save(user);
        userPhotoService.deleteFile(fileName);
    }

    @Transactional
    public User patch(Long id, UserUpdateDto dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User " + id + " niet gevonden"));

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("E-mailadres is al in gebruik.");
            }
            user.setEmail(dto.getEmail());
        }

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        applyIfPresent(dto.getRole(), user::setRole);

        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException("User " + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

}
