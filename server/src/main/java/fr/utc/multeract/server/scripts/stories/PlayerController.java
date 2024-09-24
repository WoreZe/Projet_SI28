package fr.utc.multeract.server.scripts.stories;

import com.fasterxml.jackson.annotation.JsonValue;
import fr.utc.multeract.server.models.json.HistoryUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerController {
    private final HistoryUser user;
    private final List<PlayerAction> playerActions;

    public enum VibrateStrength {
        LOW, MEDIUM, HIGH
    }
    PlayerController(HistoryUser user) {
        this.user = user;
        playerActions = new ArrayList<>();
    }

    public HistoryUser getUser() {
        return user;
    }

    public Object getAnswer(){
        if(user.getCurrentResponse() == null) return null;
        return user.getCurrentResponse().getValue();
    }

    public List<PlayerAction> getPlayerActions() {
        return playerActions;
    }

    public void vibrate() {
        vibrate(1000, VibrateStrength.MEDIUM);
    }

    public void vibrate(int duration, VibrateStrength strength) {
        playerActions.add(new VibratePlayerAction(duration, strength));
    }


    public abstract static class PlayerAction {
        String type;

        PlayerAction(String type) {
            this.type = type;
        }

        @JsonValue
        protected Map<String, Object> toJson() {
            Map<String, Object> map = new HashMap();
            map.put("type", type);
            registerData(map);
            return map;
        }

        abstract void registerData(Map<String, Object> bag);
    }

    public static class VibratePlayerAction extends PlayerAction {
        private final int duration;
        private final VibrateStrength strength;

        public VibratePlayerAction(int duration, VibrateStrength strength) {
            super("VIBRATE");
            this.duration = duration;
            this.strength = strength;
        }

        @Override
        void registerData(Map<String, Object> bag) {
            bag.put("duration", duration);
            bag.put("strength", strengthToFloat());
        }

        private Float strengthToFloat() {
            return switch (strength) {
                case LOW -> 0.25f;
                case MEDIUM -> 0.5f;
                case HIGH -> 1f;
            };
        }
    }

    public static class FlashPlayerAction extends PlayerAction {
        private final boolean on;
        private int strength;

        public FlashPlayerAction(boolean on, int strength) {
            super("FLASHLIGHT");
            this.on = on;
            this.strength = strength;
        }

        @Override
        void registerData(Map<String, Object> bag) {
            bag.put("on", on);
            if(strength != 0 && on){
                if(strength < 0) strength = 0;
                if(strength > 255) strength = 255;
                bag.put("level", strength);
            }
        }
    }

    public static class DelayPlayerAction extends PlayerAction {
        private final int duration;

        public DelayPlayerAction(int duration) {
            super("DELAY");
            this.duration = duration;
        }

        @Override
        void registerData(Map<String, Object> bag) {
            bag.put("duration", duration);
        }
    }
    public void delay(int duration) {
        playerActions.add(new DelayPlayerAction(duration));
    }

    public void flash() {
        playerActions.add(new FlashPlayerAction(true,0));
    }
    public void flash(int strength) {
        playerActions.add(new FlashPlayerAction(true,strength));
    }
    public void unFlash() {
        playerActions.add(new FlashPlayerAction(false,0));
    }

}
