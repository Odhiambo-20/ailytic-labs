package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "drones")
public class Drone {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String type;
    private String description;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> specifications;
    private String image;
    private String price;
    private Double rating;
    private Integer reviews;
    private String flightTime;
    private String range;

    public String getId() {
        return id;
    }
}
