package nl.helicenter.flightmaster.service;

import nl.helicenter.flightmaster.model.UserPhoto;
import nl.helicenter.flightmaster.repository.FileUploadRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class UserPhotoService {

    private final Path fileStoragePath;
    private final FileUploadRepository repo;

    public UserPhotoService(@Value("${my.upload_location}") String uploadLocation,
                            FileUploadRepository repo) throws IOException {

        String normalized = Objects.requireNonNull(uploadLocation, "upload location mag niet null zijn")
                .replace("\\", "/");
        this.fileStoragePath = Path.of(normalized).toAbsolutePath().normalize();
        this.repo = repo;

        Files.createDirectories(this.fileStoragePath);
    }

    public String storeFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Geen bestand ontvangen");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        fileName = fileName.replace("\\", "/");
        fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
        if (fileName.isBlank()) {
            throw new IllegalArgumentException("Lege bestandsnaam");
        }

        Path target = this.fileStoragePath.resolve(fileName).normalize();

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }

        repo.save(new UserPhoto(fileName));
        return fileName;
    }

    public Resource downLoadFile(String fileName) {
        Path path = this.fileStoragePath.resolve(fileName).normalize();

        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Issue in reading the file", e);
        }

        throw new RuntimeException("the file doesn't exist or not readable");
    }

    public void deleteFile(String fileName) {
        Path path = this.fileStoragePath.resolve(fileName).normalize();
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Issue deleting the file", e);
        }
        repo.deleteById(fileName);
    }
}
