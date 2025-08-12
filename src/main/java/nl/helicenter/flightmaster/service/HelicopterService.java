package nl.helicenter.flightmaster.service;

import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.repository.HelicopterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HelicopterService {

    private final HelicopterRepository helicopterRepository;

    public HelicopterService(HelicopterRepository helicopterRepository) {
        this.helicopterRepository = helicopterRepository;
    }

    public Helicopter addHelicopter(Helicopter helicopter) {
        return helicopterRepository.save(helicopter);
    }

    public List<Helicopter> getAllHelicopters() {
        return helicopterRepository.findAll();
    }

    public Helicopter getHelicopterById(Long id) {
        return helicopterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Helicopter met id " + id + " niet gevonden."));
    }
}
