package com.bellatechnologies.backend.model;

import jakarta.persistence.*;

import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "robots")
public class Robot {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    private String name;
    private String type;
    private String description;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "text")
    private List<String> capabilities;
    private String image;
    private String price;
    private Double rating;
    private Integer reviews;

    public String getId() {
        return id;
    }
}
