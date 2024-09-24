package fr.utc.multeract.server.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;

import java.util.HashMap;
import java.util.Map;

public class WebsocketMessageWrapper<T> {

    private final WebsocketMessageType type;

    private final T message;

    private final String preparedPayload;

    WebsocketMessageWrapper(WebsocketMessageType type, T payload) {
        this.type = type;
        this.message = payload;
        this.preparedPayload = preparePayload();
    }

    public static WebsocketMessageWrapper<?> fromJson(String payload) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            var map = objectMapper.readValue(payload, Map.class);
            WebsocketMessageType type = WebsocketMessageType.valueOf((String) map.get("type"));
            Object data = map.get("data");
            return new WebsocketMessageWrapper<>(type, data);
        } catch (Exception e) {
            return null;
        }
    }

    private String preparePayload() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", type);
        payload.put("data", prepareData(message));
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (Exception e) {
            return null;
        }
    }

    protected Object prepareData(T message) {
        return message;
    }

    public WebSocketMessage<?> getWebSocketMessage() {
        return new TextMessage(preparedPayload);
    }

    public WebsocketMessageType getType() {
        return type;
    }

    public T getPayload() {
        return message;
    }
}
