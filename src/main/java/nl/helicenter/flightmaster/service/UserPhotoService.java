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
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class UserPhotoService {
    private final Path fileStoragePath;
    private final String fileStorageLocation;
    private final FileUploadRepository repo;

    public UserPhotoService(@Value("${my.upload_location}") String fileStorageLocation,
                        FileUploadRepository repo) throws IOException {
        this.fileStorageLocation = fileStorageLocation;
        this.fileStoragePath = Paths.get(fileStorageLocation).toAbsolutePath().normalize();
        this.repo = repo;

        Files.createDirectories(fileStoragePath);
    }

    public String storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path filePath = Paths.get(fileStoragePath + "\\" + fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        repo.save(new UserPhoto(fileName));
        return fileName;
    }

    public Resource downLoadFile(String fileName) {
        Path path = Paths.get(fileStorageLocation).toAbsolutePath().resolve(fileName);
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
        try {
            Files.deleteIfExists(Paths.get(fileStoragePath + "\\" + fileName));
        } catch (IOException e) {
            throw new RuntimeException("Issue deleting the file", e);
        }
        repo.deleteById(fileName);
    }
}
