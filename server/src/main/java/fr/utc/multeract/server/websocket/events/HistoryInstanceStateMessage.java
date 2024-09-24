package fr.utc.multeract.server.websocket.events;

import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.websocket.WebsocketMessageType;

public class HistoryInstanceStateMessage extends StoryInstanceMessage {

    private final StoryInstance instance;

    public HistoryInstanceStateMessage(StoryInstance instance) {
        super(instance.getId(), WebsocketMessageType.HISTORY_INSTANCE_STATE);
        this.instance = instance;
    }

    @Override
    protected Object data() {
        return instance;
    }
}
