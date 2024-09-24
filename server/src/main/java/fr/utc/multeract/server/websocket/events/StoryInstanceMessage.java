package fr.utc.multeract.server.websocket.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.utc.multeract.server.websocket.WebsocketMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;

import java.util.HashMap;
import java.util.Map;

abstract public class StoryInstanceMessage {
    private static final Logger log = LoggerFactory.getLogger(StoryInstanceMessage.class);
    private final Long historyId;
    private boolean isForReadOnly;
    private WebsocketMessageType type;

    public StoryInstanceMessage(Long historyId, WebsocketMessageType type) {
        this.historyId = historyId;
        this.type = type;
    }

    public StoryInstanceMessage(Long historyId, boolean isForReadOnly) {
        this.historyId = historyId;
        this.isForReadOnly = isForReadOnly;
    }

    public boolean isForReadOnly() {
        return isForReadOnly;
    }

    public void setForReadOnly(boolean forReadOnly) {
        isForReadOnly = forReadOnly;
    }

    public Long getHistoryId() {
        return historyId;
    }

    public String getWebSocketMessagePayload(ObjectMapper mapper) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", type);
            payload.put("data", data());
            payload.put("at", System.currentTimeMillis());
            return mapper.writeValueAsString(payload);
        } catch (Exception e) {
            log.error("Error while serializing message", e);
            return null;
        }
    }

    abstract protected Object data();

    public TextMessage getWebSocketMessage(ObjectMapper mapper) {
        return new TextMessage(getWebSocketMessagePayload(mapper));
    }
}
