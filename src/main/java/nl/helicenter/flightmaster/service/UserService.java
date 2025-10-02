package nl.helicenter.flightmaster.service;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.repository.UserRepository;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.core.io.Resource;
import nl.helicenter.flightmaster.model.UserPhoto;
import nl.helicenter.flightmaster.repository.FileUploadRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FileUploadRepository fileUploadRepository;
    private final UserPhotoService userPhotoService;

    public UserService(UserRepository userRepository, FileUploadRepository fileUploadRepository, UserPhotoService userPhotoService) {
        this.userRepository = userRepository;
        this.userPhotoService = userPhotoService;
        this.fileUploadRepository = fileUploadRepository;
    }

    public String registerUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mailadres is al in gebruik.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        if (dto.getRole() == null || !"ADMIN".equalsIgnoreCase(dto.getRole())) {
            user.setRole("USER");
        } else {
            user.setRole("USER");
        }
        userRepository.save(user);
        return user.getEmail();
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

}
