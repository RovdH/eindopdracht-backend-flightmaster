package nl.helicenter.flightmaster.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Getter
@Setter
public class Pdf {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Event event;

    @Column(nullable = false, length = 200)
    private String fileName;

    @Column(nullable = false)
    private String fileExtension;

    @Lob
    @Basic
    @Column(nullable = false)
    private byte[] content;

    @Column(nullable = false)
    private long sizeBytes;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}
