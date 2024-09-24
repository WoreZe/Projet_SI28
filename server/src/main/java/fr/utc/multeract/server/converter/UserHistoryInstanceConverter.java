package fr.utc.multeract.server.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.utc.multeract.server.models.json.HistoryUser;
import jakarta.persistence.AttributeConverter;

import java.util.HashMap;
import java.util.Map;

public class UserHistoryInstanceConverter implements AttributeConverter<Map<String, HistoryUser>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, HistoryUser> stringObjectMap) {
        try {
            return new ObjectMapper().writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    @Override
    public Map<String, HistoryUser> convertToEntityAttribute(String s) {
        if(s == null) return new HashMap<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Map map = mapper.readValue(s, Map.class);
            if(map == null) return new HashMap<>();
            Map<String, HistoryUser> result = new HashMap<>();
            map.forEach((k,v) -> result.put(k.toString(), mapper.convertValue(v, HistoryUser.class)));
            return result;
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
}
