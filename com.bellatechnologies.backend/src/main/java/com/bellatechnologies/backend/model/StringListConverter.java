package com.bellatechnologies.backend.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.ArrayList;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> TYPE = new TypeReference<>() {};

    @Override
    public String convertToDatabaseColumn(List<String> values) {
        try {
            return values == null ? "[]" : MAPPER.writeValueAsString(values);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to serialize string list", exception);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String value) {
        try {
            return value == null || value.isBlank() ? new ArrayList<>() : MAPPER.readValue(value, TYPE);
        } catch (Exception exception) {
            throw new IllegalArgumentException("Unable to deserialize string list", exception);
        }
    }
}
