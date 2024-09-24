package fr.utc.multeract.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListConverter implements AttributeConverter<List<Object>, String> {
    @Override
    public String convertToDatabaseColumn(List<Object> stringObjectMap) {
        try {
            return new ObjectMapper().writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public List<Object> convertToEntityAttribute(String s) {
        if(s == null) return new ArrayList<>();
        try {
            return new ObjectMapper().readValue(s, ArrayList.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
