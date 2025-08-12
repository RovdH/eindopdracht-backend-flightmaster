package nl.helicenter.flightmaster.controller;

import nl.helicenter.flightmaster.model.Helicopter;
import nl.helicenter.flightmaster.service.HelicopterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/helicopters")
public class HelicopterController {

    private final HelicopterService helicopterService;

    public HelicopterController(HelicopterService helicopterService) {
        this.helicopterService = helicopterService;
    }

    @PostMapping
    public ResponseEntity<Helicopter> create(@RequestBody Helicopter helicopter) {
        return ResponseEntity.ok(helicopterService.addHelicopter(helicopter));
    }

    @GetMapping
    public ResponseEntity<List<Helicopter>> getAll() {
        return ResponseEntity.ok(helicopterService.getAllHelicopters());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Helicopter> getById(@PathVariable Long id) {
        return ResponseEntity.ok(helicopterService.getHelicopterById(id));
    }
}
