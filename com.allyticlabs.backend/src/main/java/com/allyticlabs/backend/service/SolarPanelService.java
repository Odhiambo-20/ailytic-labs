package com.allyticlabs.backend.service;

import com.allyticlabs.backend.exception.ResourceNotFoundException;
import com.allyticlabs.backend.model.SolarPanel;
import com.allyticlabs.backend.repository.SolarPanelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SolarPanelService {
    private final SolarPanelRepository solarPanelRepository;

    public SolarPanelService(SolarPanelRepository solarPanelRepository) {
        this.solarPanelRepository = solarPanelRepository;
    }

    public List<SolarPanel> getAllSolarPanels() {
        return solarPanelRepository.findAll();
    }

    public List<SolarPanel> getSolarPanelsByType(String type) {
        return solarPanelRepository.findByType(type);
    }

    public SolarPanel getSolarPanelById(String id) {
        SolarPanel solarPanel = solarPanelRepository.findById(id);
        if (solarPanel == null) {
            throw new ResourceNotFoundException("Solar Panel not found with id: " + id);
        }
        return solarPanel;
    }

    public SolarPanel createSolarPanel(SolarPanel solarPanel) {
        return solarPanelRepository.save(solarPanel);
    }

    public SolarPanel updateSolarPanel(String id, SolarPanel solarPanel) {
        if (solarPanelRepository.findById(id) == null) {
            throw new ResourceNotFoundException("Solar Panel not found with id: " + id);
        }
        solarPanel.setId(id);
        return solarPanelRepository.update(solarPanel);
    }

    public void deleteSolarPanel(String id) {
        if (solarPanelRepository.findById(id) == null) {
            throw new ResourceNotFoundException("Solar Panel not found with id: " + id);
        }
        solarPanelRepository.delete(id);
    }
}