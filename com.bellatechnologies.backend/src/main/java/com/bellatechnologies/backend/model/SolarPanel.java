package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "solar_panels")
public class SolarPanel {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String type;
    private String description;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> features;
    private String image;
    private String power;
    private String efficiency;
    private String warranty;
    private String price;

    public String getId() {
        return id;
    }
}
