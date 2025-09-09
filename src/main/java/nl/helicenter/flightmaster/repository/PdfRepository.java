package nl.helicenter.flightmaster.repository;

import nl.helicenter.flightmaster.model.Pdf;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PdfRepository extends JpaRepository<Pdf, Long> {
        List<Pdf> findByEventId(Long eventId);
    }

