package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.PdfResponseDto;
import nl.helicenter.flightmaster.model.Event;
import nl.helicenter.flightmaster.model.Flight;
import nl.helicenter.flightmaster.model.Passenger;
import nl.helicenter.flightmaster.model.Pdf;
import nl.helicenter.flightmaster.repository.EventRepository;
import nl.helicenter.flightmaster.repository.FlightRepository;
import nl.helicenter.flightmaster.repository.PassengerRepository;
import nl.helicenter.flightmaster.repository.PdfRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static java.util.Arrays.stream;
import static org.apache.coyote.http11.Constants.a;

@Service
public class PdfService {

    private final EventRepository eventRepository;
    private final FlightRepository flightRepository;
    private final PassengerRepository passengerRepository;
    private final PdfRepository pdfRepository;

    public PdfService(EventRepository eventRepository,
                      FlightRepository flightRepository,
                      PassengerRepository passengerRepository,
                      PdfRepository pdfRepository) {
        this.eventRepository = eventRepository;
        this.flightRepository = flightRepository;
        this.passengerRepository = passengerRepository;
        this.pdfRepository = pdfRepository;
    }

    @Transactional
    public PdfResponseDto generatePassengerList(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException("Event" + eventId + "niet gevonden"));

        List<Flight> flights = flightRepository.findByEvent_Id(eventId)
                .stream().sorted((a, b) -> a.getStartTime().compareTo(b.getStartTime())).toList();

        StringBuilder sb = new StringBuilder();
        sb.append("Passagierslijsten" + event.getLocation() + "Event")
                .append(event.getDate()).append("@").append(event.getLocation()).append("\n\n");

        for (Flight flight : flights) {
            sb.append("Flight ").append(flight.getFlightNumber()).append(" (").append(flight.getStartTime()).append(")\n");

            List<Passenger> pax = passengerRepository.findAllByFlight_Id(flight.getId());
            for (Passenger passenger : pax) {
                sb.append(" - ").append(passenger.getFirstName()).append(" ").append(passenger.getLastName())
                        .append(" | ").append(passenger.getWeight()).append(" kg\n");
            }
            sb.append("\n");
        }

        byte[] pdfBytes = simplePdf(sb.toString());
        String fileName = "Passagierslijst" + event.getLocation() + event.getDate() + ".pdf";

        Pdf pdf = new Pdf();
        pdf.setEvent(event);
        pdf.setFileName(fileName);
        pdf.setFileExtension("application/pdf");
        pdf.setContent(pdfBytes);
        pdf.setSizeBytes(pdfBytes.length);
        Pdf saved = pdfRepository.save(pdf);
        return toDto(saved);
    }

    @Transactional(readOnly = true)
    public PdfResponseDto getMeta(Long pdfId) {
        return pdfRepository.findById(pdfId).map(this::toDto)
                .orElseThrow(() -> new EntityNotFoundException("PDF " + pdfId + " niet gevonden."));
    }

    @Transactional(readOnly = true)
    public byte[] getBytes(Long pdfId) {
        Pdf pdf = pdfRepository.findById(pdfId)
                .orElseThrow(() -> new EntityNotFoundException("PDF " + pdfId + " niet gevonden."));
        return pdf.getContent();
    }

    @Transactional(readOnly = true)
    public List<PdfResponseDto> listByEvent(Long eventId) {
        return pdfRepository.findByEventId(eventId).stream().map(this::toDto).toList();
    }

    private PdfResponseDto toDto(Pdf d) {
        PdfResponseDto dto = new PdfResponseDto();
        dto.setId(d.getId());
        dto.setEventId(d.getEvent().getId());
        dto.setFileName(d.getFileName());
        dto.setFileExtension(d.getFileExtension());
        dto.setSizeBytes(d.getSizeBytes());
        dto.setCreatedAt(d.getCreatedAt());
        return dto;
    }
    private byte[] simplePdf(String content) {
        return ("OUTPUT_PDF\n\n" + content).getBytes(StandardCharsets.UTF_8);
    }
}
