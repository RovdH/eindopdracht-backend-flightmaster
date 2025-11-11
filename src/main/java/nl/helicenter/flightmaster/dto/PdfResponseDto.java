package nl.helicenter.flightmaster.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class PdfResponseDto {
    private Long id;
    private Long eventId;
    private String fileName;
    private String fileExtension;
    private long sizeBytes;
    private Instant createdAt;
}
