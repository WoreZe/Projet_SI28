package fr.utc.multeract.server.websocket.events;

import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.scripts.stories.PlayerController;
import fr.utc.multeract.server.websocket.WebsocketMessageType;

import java.util.List;

public class PlayerActionListEvent extends StoryInstanceMessage {

    private final List<PlayerController.PlayerAction> events;

    public PlayerActionListEvent(StoryInstance instance, List<PlayerController.PlayerAction> events) {
        super(instance.getId(), WebsocketMessageType.PLAYER_ACTION_LIST);
        this.events = events;
    }


    @Override
    protected Object data() {
        return events;
    }
}
