package fr.utc.multeract.server.endpoints;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.services.HistoryInstanceService;
import fr.utc.multeract.server.websocket.WebsocketMessageType;
import fr.utc.multeract.server.websocket.WebsocketMessageWrapper;
import fr.utc.multeract.server.websocket.events.StoryInstanceMessage;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WebsocketHistoryMessageHandler extends TextWebSocketHandler {

    private static final ConcurrentHashMap<Long, List<SessionWrapper>> sessions = new ConcurrentHashMap<>();
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(WebsocketHistoryMessageHandler.class);

    @Autowired
    HistoryInstanceService historyInstanceService;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        var wrapper = new SessionWrapper(session);
        Optional<StoryInstance> instance = historyInstanceService.getHistoryInstance(wrapper.getInstance());
        if(instance.isEmpty()){
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }
        addOrCreateSession(wrapper);
        wrapper.sendToSession(WebsocketMessageType.HISTORY_INSTANCE_STATE,instance.get());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        var payload = message.getPayload();
        if (payload.isEmpty()) {
            return;
        }
        SessionWrapper wrapper = new SessionWrapper(session);
        unwrapMessage(wrapper,payload);
    }

    private void unwrapMessage(SessionWrapper sessionWrapper, String payload) {
        WebsocketMessageWrapper<?> message = WebsocketMessageWrapper.fromJson(payload);
        if (message == null) {
            return;
        }
        switch (message.getType()) {
            case HISTORY_INSTANCE_STATE:
                Map<String, Object> event = (Map<String, Object>) message.getPayload();
                break;
            case USER_READ_MESSAGE:
            default:
                break;
        }
    }

    private void addOrCreateSession(SessionWrapper wrapper) {
        sessions.computeIfAbsent(wrapper.instance, k -> new ArrayList<>()).add(wrapper);
    }

    @Transactional
    @EventListener(StoryInstanceMessage.class)
    public void sendToUserMessageEvent(StoryInstanceMessage event) {
        sendToSession(event);
    }

    protected void sendToSession(StoryInstanceMessage event) {
        List<SessionWrapper> sessionList = sessions.get(event.getHistoryId());
        if (sessionList == null) {
            return;
        }
        sessionList = sessionList.stream().toList();
        for(SessionWrapper session: sessionList){
            if(!session.isReadOnly() && event.isForReadOnly()){
                continue;
            }
            try {
                session.session.sendMessage(event.getWebSocketMessage(objectMapper));
            } catch (Exception e) {
                sessions.get(event.getHistoryId()).removeIf(s -> s.session.getId().equals(session.session.getId()));
            }
        }
    }

    private class SessionWrapper {
        private final long history, instance;
        private final boolean readOnly;
        public WebSocketSession session;
        public SessionWrapper(WebSocketSession session) {
            var attrs = session.getAttributes();
            this.history = (long) attrs.get("history_id");
            this.instance = (long) attrs.get("instance");
            this.readOnly = (boolean) attrs.get("readonly");
            this.session = session;
        }

        public boolean isReadOnly() {
            return readOnly;
        }

        public long getInstance() {
            return instance;
        }

        public long getHistory() {
            return history;
        }

        public String getUsername() {
            return session.getAttributes().get("username").toString();
        }

        public void sendToSession(WebsocketMessageType type, Object o) throws IOException {
            Map<String,Object> object = Map.of("type",type, "timestamp", System.currentTimeMillis(),"data",o);
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(object)));
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof SessionWrapper){
                return ((SessionWrapper) obj).session.getId().equals(session.getId());
            } else if (obj instanceof WebSocketSession){
                return session.getId().equals(((WebSocketSession) obj).getId());
            }
            return false;
        }
    }
}

