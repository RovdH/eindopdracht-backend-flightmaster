package nl.helicenter.flightmaster.controller;

import jakarta.validation.constraints.Positive;
import nl.helicenter.flightmaster.dto.PdfResponseDto;
import nl.helicenter.flightmaster.service.PdfService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
@Validated
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/events/{eventId}/pdfs")
    public ResponseEntity<PdfResponseDto> generate(@PathVariable @Positive Long eventId) {
        var dto = pdfService.generatePassengerList(eventId);
        return ResponseEntity.status(201).body(dto);
    }

    @GetMapping("/events/{eventId}/pdfs")
    public ResponseEntity<List<PdfResponseDto>> list(@PathVariable @Positive Long eventId) {
        return ResponseEntity.ok(pdfService.listByEvent(eventId));
    }

    @GetMapping("/pdfs/{id}")
    public ResponseEntity<PdfResponseDto> getMeta(@PathVariable @Positive Long id) {
        return ResponseEntity.ok(pdfService.getMeta(id));
    }

    @GetMapping("/pdfs/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable @Positive Long id) {
        PdfResponseDto meta = pdfService.getMeta(id);
        byte[] bytes = pdfService.getBytes(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + meta.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(bytes.length)
                .body(bytes);
    }
}
