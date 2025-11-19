package com.allyticlabs.backend.controller;

import com.allyticlabs.backend.model.SolarPanel;
import com.allyticlabs.backend.service.SolarPanelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/solar-panels")
public class SolarPanelController {
    private final SolarPanelService solarPanelService;

    public SolarPanelController(SolarPanelService solarPanelService) {
        this.solarPanelService = solarPanelService;
    }

    @GetMapping
    public List<SolarPanel> getAllSolarPanels(@RequestParam(required = false) String type) {
        if (type != null) {
            return solarPanelService.getSolarPanelsByType(type);
        }
        return solarPanelService.getAllSolarPanels();
    }

    @GetMapping("/{id}")
    public SolarPanel getSolarPanelById(@PathVariable String id) {
        return solarPanelService.getSolarPanelById(id);
    }

    @PostMapping
    public SolarPanel createSolarPanel(@Valid @RequestBody SolarPanel solarPanel) {
        return solarPanelService.createSolarPanel(solarPanel);
    }

    @PutMapping("/{id}")
    public SolarPanel updateSolarPanel(@PathVariable String id, @Valid @RequestBody SolarPanel solarPanel) {
        return solarPanelService.updateSolarPanel(id, solarPanel);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSolarPanel(@PathVariable String id) {
        solarPanelService.deleteSolarPanel(id);
        return ResponseEntity.noContent().build();
    }
}