package fr.utc.multeract.server.websocket.events;

import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.websocket.WebsocketMessageType;

public class DirtyStateScreen extends StoryInstanceMessage {

    private final StoryInstance instance;

    public DirtyStateScreen(StoryInstance instance) {
        super(instance.getId(), WebsocketMessageType.DIRTYSATE_SCREEN);
        this.instance = instance;
        setForReadOnly(true);
    }

    @Override
    protected Object data() {
        return instance.getHistory();
    }
}
