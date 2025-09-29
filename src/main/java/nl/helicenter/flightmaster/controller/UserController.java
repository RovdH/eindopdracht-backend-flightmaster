package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.service.UserPhotoService;
import nl.helicenter.flightmaster.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

@RestController
@RequestMapping("/users")
@Validated
public class UserController {

    private final UserService userService;
    private final UserPhotoService userPhotoService;

    public UserController(UserService userService, UserPhotoService userPhotoService) {
        this.userService = userService;
        this.userPhotoService = userPhotoService;
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

    @PostMapping("/{id}/photo")
    public ResponseEntity<User> addPhotoToUser(@PathVariable("id") Long userId,
                                               @RequestBody MultipartFile file) throws IOException {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/")
                .path(Objects.requireNonNull(userId.toString()))
                .path("/photo")
                .toUriString();

        String fileName = userPhotoService.storeFile(file);       // opslaan op disk
        User updated = userService.assignPhotoToUser(fileName, userId); // koppelen aan user

        return ResponseEntity.created(URI.create(url)).body(updated);
    }

    @PutMapping("/{id}/photo")
    public ResponseEntity<User> overwriteUserPhoto(@PathVariable("id") Long userId,
                                                   @RequestBody MultipartFile file) throws IOException {
        String fileName = userPhotoService.storeFile(file);       // REPLACE_EXISTING
        User updated = userService.assignPhotoToUser(fileName, userId);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getUserPhoto(@PathVariable("id") Long userId,
                                                 HttpServletRequest request) {
        Resource resource = userService.getPhotoFromUser(userId);

        String mimeType;
        try {
            mimeType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mimeType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline;fileName=" + resource.getFilename())
                .body(resource);
    }

    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Void> deleteUserPhoto(@PathVariable("id") Long userId) {
        userService.deletePhotoFromUser(userId);
        return ResponseEntity.noContent().build();
    }

}
