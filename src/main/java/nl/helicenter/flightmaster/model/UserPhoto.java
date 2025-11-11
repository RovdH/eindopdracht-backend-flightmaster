package nl.helicenter.flightmaster.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UserPhoto {

    @Id
    private String fileName;

    public UserPhoto() {
    }

    public UserPhoto(String fileName) {
        this.fileName = fileName;
    }
    public String getFileName() {
        return fileName;
    }
}
