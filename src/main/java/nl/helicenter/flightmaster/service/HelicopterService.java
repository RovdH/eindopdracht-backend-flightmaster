package nl.helicenter.flightmaster.service;

import jakarta.persistence.EntityNotFoundException;
import nl.helicenter.flightmaster.dto.HelicopterRequestDto;
import nl.helicenter.flightmaster.dto.HelicopterResponseDto;
import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.repository.HelicopterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HelicopterService {

    private final HelicopterRepository helicopterRepository;

    public HelicopterService(HelicopterRepository helicopterRepository) {
        this.helicopterRepository = helicopterRepository;
    }

    public HelicopterResponseDto addHelicopter(HelicopterRequestDto dto) {
        Helicopter helicopter = new Helicopter();
        helicopter.setCallSign(dto.getCallSign());
        helicopter.setType(dto.getType());
        helicopter.setCapacity(dto.getCapacity());
        helicopter.setFuelCapacity(dto.getFuelCapacity());
        helicopter.setFuelUsage(dto.getFuelUsage());
        helicopter.setAvailable(dto.getAvailable());

        Helicopter saved = helicopterRepository.save(helicopter);
        return mapToResponseDto(saved);
    }

    public List<HelicopterResponseDto> getAllHelicopters() {
        return helicopterRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public HelicopterResponseDto getHelicopterById(Long id) {
        Helicopter helicopter = helicopterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Helikopter met id " + id + " niet gevonden."));
        return mapToResponseDto(helicopter);
    }

    private HelicopterResponseDto mapToResponseDto(Helicopter helicopter) {
        HelicopterResponseDto dto = new HelicopterResponseDto();
        dto.setId(helicopter.getId());
        dto.setCallSign(helicopter.getCallSign());
        dto.setType(helicopter.getType());
        dto.setCapacity(helicopter.getCapacity());
        dto.setFuelCapacity(helicopter.getFuelCapacity());
        dto.setFuelUsage(helicopter.getFuelUsage());
        dto.setAvailable(helicopter.isAvailable());
        return dto;
    }

    @Transactional
    public void delete(Long id) {
        if (!helicopterRepository.existsById(id)) {
            throw new EntityNotFoundException("Helikopter met id " + id + " niet gevonden");
        }
        helicopterRepository.deleteById(id);
    }
}
