package nl.helicenter.flightmaster.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.service.UserPhotoService;
import nl.helicenter.flightmaster.service.UserService;
import org.springframework.http.HttpStatus;
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
        Long id = userService.registerUser(dto);
        return ResponseEntity.created(URI.create("/users/" + id))
                .contentType(MediaType.TEXT_PLAIN)
                .body("Gebruiker geregistreerd met id: " + id);
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<User> addPhotoToUser(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        String url = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/")
                .path(Objects.requireNonNull(id.toString()))
                .path("/photo")
                .toUriString();

        String fileName = userPhotoService.storeFile(file);
        User updated = userService.assignPhotoToUser(fileName, id);

        return ResponseEntity.created(URI.create(url)).body(updated);
    }

    @PutMapping("/{id}/photo")
    public ResponseEntity<User> overwriteUserPhoto(@PathVariable("id") Long id,
                                                   @RequestBody MultipartFile file) throws IOException {
        String fileName = userPhotoService.storeFile(file);
        User updated = userService.assignPhotoToUser(fileName, id);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<Resource> getUserPhoto(@PathVariable("id") Long id,
                                                 HttpServletRequest request) {
        Resource resource = userService.getPhotoFromUser(id);

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
    public ResponseEntity<Void> deleteUserPhoto(@PathVariable("id") Long id) {
        userService.deletePhotoFromUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        userService.delete(id);
    }

}
