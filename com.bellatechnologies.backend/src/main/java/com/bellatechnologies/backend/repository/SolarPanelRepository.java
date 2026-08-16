package com.bellatechnologies.backend.repository;

import com.bellatechnologies.backend.model.SolarPanel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SolarPanelRepository extends JpaRepository<SolarPanel, String> {
    List<SolarPanel> findByType(String type);
}
