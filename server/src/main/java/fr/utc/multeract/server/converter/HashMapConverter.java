package fr.utc.multeract.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        if(stringObjectMap == null) stringObjectMap = new HashMap<>();
        try {
            return new ObjectMapper().writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        if(s == null) return new HashMap<>();
        if(s.equals("null")) return new HashMap<>();
        try {
            return new ObjectMapper().readValue(s, HashMap.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
