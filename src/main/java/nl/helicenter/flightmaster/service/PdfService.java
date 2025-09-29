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

import java.io.ByteArrayOutputStream;
import java.util.Comparator;
import java.util.List;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

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
                .stream()
                .sorted(Comparator.comparing(Flight::getStartTime))
                .toList();

//        StringBuilder sb = new StringBuilder();
//        sb.append("Passagierslijsten" + event.getLocation() + "Event")
//                .append(event.getDate()).append("@").append(event.getLocation()).append("\n\n");
//
//        for (Flight flight : flights) {
//            sb.append("Flight ").append(flight.getFlightNumber()).append(" (").append(flight.getStartTime()).append(")\n");
//
//            List<Passenger> pax = passengerRepository.findAllByFlight_Id(flight.getId());
//            for (Passenger passenger : pax) {
//                sb.append(" - ").append(passenger.getFirstName()).append(" ").append(passenger.getLastName())
//                        .append(" | ").append(passenger.getWeight()).append(" kg").append(" | ").append(passenger.getEmail()).append("\n");
//            }
//            sb.append("\n");
//        }
//
//        byte[] pdfBytes = simplePdf(sb.toString());
//        String fileName = "Passagierslijst" + event.getLocation() + event.getDate() + ".pdf";

        byte[] pdfBytes = createPdfBytes(event, flights);
        String fileName = "Passagierslijst_" + event.getLocation() + "_" + event.getDate() + ".pdf";


        Pdf pdf = new Pdf();
        pdf.setEvent(event);
        pdf.setFileName(fileName);
        pdf.setFileExtension("application/pdf");
        pdf.setContent(pdfBytes);
        pdf.setSizeBytes(pdfBytes.length);
        Pdf saved = pdfRepository.save(pdf);
        return toDto(saved);
    }

    private byte[] createPdfBytes(Event event, List<Flight> flights) {
        try (ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 36, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            var title = new Paragraph(
                    "Passagierslijst – " + event.getDate() + " @ " + event.getLocation(),
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)
            );
            title.setSpacingAfter(12f);
            doc.add(title);

            var meta = new Paragraph(
                    "Eventdatum: " + event.getDate() + "\nLocatie: " + event.getLocation(),
                    FontFactory.getFont(FontFactory.HELVETICA, 11)
            );
            meta.setSpacingAfter(12f);
            doc.add(meta);

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font cellFont   = FontFactory.getFont(FontFactory.HELVETICA, 10);

            for (Flight flight : flights) {
                Paragraph flightHeader = new Paragraph(
                        "Vlucht " + flight.getFlightNumber()
                                + "  (" + flight.getStartTime() + "–"
                                + flight.getStartTime().plusMinutes((long) flight.getEvent().getFlightTime()) + ")  "
                                + "Heli: " + flight.getHelicopter().getCallSign(),
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 13)
                );
                flightHeader.setSpacingBefore(10f);
                flightHeader.setSpacingAfter(6f);
                doc.add(flightHeader);

                PdfPTable table = new PdfPTable(new float[]{3f, 5f, 2f});
                table.setWidthPercentage(100);

                table.addCell(headerCell("Naam", headerFont));
                table.addCell(headerCell("E-mail", headerFont));
                table.addCell(headerCell("Gewicht (kg)", headerFont));

                List<Passenger> pax = passengerRepository.findAllByFlight_Id(flight.getId());
                if (pax.isEmpty()) {
                    PdfPCell empty = new PdfPCell(new Phrase("— geen passagiers —", cellFont));
                    empty.setColspan(3);
                    table.addCell(empty);
                } else {
                    for (Passenger p : pax) {
                        table.addCell(bodyCell(p.getFirstName() + " " + p.getLastName(), cellFont));
                        table.addCell(bodyCell(p.getEmail(), cellFont));
                        table.addCell(bodyCell(String.valueOf(p.getWeight()), cellFont));
                    }
                }

                doc.add(table);
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new IllegalStateException("Kon PDF niet genereren", e);
        }
    }

    private PdfPCell headerCell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setHorizontalAlignment(Element.ALIGN_LEFT);
        c.setPadding(6f);
        return c;
    }

    private PdfPCell bodyCell(String text, Font font) {
        PdfPCell c = new PdfPCell(new Phrase(text, font));
        c.setPadding(5f);
        return c;
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
//    private byte[] simplePdf(String content) {
//        return ("OUTPUT_PDF\n\n" + content).getBytes(StandardCharsets.UTF_8);
//    }
}
