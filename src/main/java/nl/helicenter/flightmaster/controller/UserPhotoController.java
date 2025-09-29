package nl.helicenter.flightmaster.controller;

import nl.helicenter.flightmaster.service.UserPhotoService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/user-photos")
public class UserPhotoController {

    private final UserPhotoService userPhotoService;

    public UserPhotoController(UserPhotoService userPhotoService) {
        this.userPhotoService = userPhotoService;
    }

    @PostMapping
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
        String name = userPhotoService.storeFile(file);
        URI location = URI.create(currentBase() + "/user-photos/" + name);
        return ResponseEntity.created(location).body(name);
    }

    @PutMapping
    public ResponseEntity<String> overwrite(@RequestParam("file") MultipartFile file) throws Exception {
        String name = userPhotoService.storeFile(file);
        URI location = URI.create(currentBase() + "/user-photos/" + name);
        return ResponseEntity.ok().location(location).body(name);
    }

    @PatchMapping
    public ResponseEntity<String> patch(@RequestParam("file") MultipartFile file) throws Exception {
        return overwrite(file);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> download(@PathVariable String fileName) {
        Resource resource = userPhotoService.downLoadFile(fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @DeleteMapping("/{fileName}")
    public ResponseEntity<Void> delete(@PathVariable String fileName) {
        userPhotoService.deleteFile(fileName);
        return ResponseEntity.noContent().build();
    }

    private String currentBase() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
    }
}
