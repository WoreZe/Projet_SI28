package fr.utc.multeract.server.scripts.stories;

import com.fasterxml.jackson.annotation.JsonValue;
import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.models.StoryStep;
import fr.utc.multeract.server.models.json.HistoryUser;
import fr.utc.multeract.server.repositories.StoryStepRepository;
import fr.utc.multeract.server.websocket.events.DirtyStateScreen;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StoryPlayer {
    private final StoryInstance instance;
    private final Map<String, PlayerController> users;
    private final int currentTick;
    private final StoryStepRepository storyStepRepository;
    //session cache is only shared between engine and html renderer this cache exist until step change
    private final Map<String, Object> _viewCache;
    private final Map<String, Object> _persistCache;
    private final ApplicationEventPublisher eventPublisher;
    private @Nullable String goToAction;
    private List<StoryStep> allSteps;
    private boolean updateStateRequest = false;

    @SuppressWarnings("unchecked")
    public StoryPlayer(StoryInstance instance, StoryStepRepository storyStepRepository, ApplicationEventPublisher eventPublisher) {
        this.storyStepRepository = storyStepRepository;
        this.eventPublisher = eventPublisher;
        this.instance = instance;
        this.users = new HashMap<>();
        for (Map.Entry<String, HistoryUser> stringHistoryUserEntry : this.instance.getUsers().entrySet()) {
            this.users.put(stringHistoryUserEntry.getValue().getUniqueId(), new PlayerController(stringHistoryUserEntry.getValue()));
        }
        Map<String, Object> cache = instance.getVariables();
        this._viewCache = new HashMap<>((Map<String, Object>) cache.getOrDefault("vCache", new HashMap<>()));
        this._persistCache = new HashMap<>((Map<String, Object>) cache.getOrDefault("uCache", new HashMap<>()));
        this.currentTick = this.instance.getTickTimer();
    }

    public StoryStep getStep() {
        return instance.getCurrentStep();
    }

    public void goTo(String name) {
        if (stepExist(name)) {
            this.goToAction = name;
        }
    }

    public boolean stepExist(String name) {
        return getAllSteps().stream().anyMatch((StoryStep item) -> Objects.equals(item.getSlug(), name));
    }

    private List<StoryStep> getAllSteps() {
        if (allSteps == null) {
            allSteps = storyStepRepository.findAllByHistoryId(instance.getHistory().getId());
        }
        return allSteps;
    }

    public PlayerController getPlayer(String uuid) {
        return this.users.get(uuid);
    }

    public PlayerController getPlayerByUsername(String username) {
        return this.users.values().stream().filter((PlayerController item) -> Objects.equals(item.getUser().getUsername(), username)).findFirst().orElse(null);
    }

    public PlayerController getRandomPlayer() {
        var alea = (int) (Math.random() * this.users.size());
        return this.users.entrySet().stream().skip(alea).findFirst().get().getValue();
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public Long timeLastStep() {
        return instance.getEnterAt().getTime();
    }

    public Map<String, Object> view() {
        return _viewCache;
    }

    public Map<String, Object> persist() {
        return _persistCache;
    }

    public boolean isInStepSinceSec(int sec) {
        return (this.timeSinceLastStep()) > sec * 1000L;
    }

    public boolean isInStepSec(int sec) {
        return Math.abs((this.timeSinceLastStep() - sec * 1000L)) < 1000L;
    }

    public Long timeSinceLastStep() {
        return System.currentTimeMillis() - timeLastStep();
    }

    @JsonValue
    public Map<String, Object> toJsonStateSave() {
        Map<String, Object> map = new HashMap<>();
        map.put("vCache", _viewCache);
        map.put("uCache", _persistCache);
        map.put("tickTimer", currentTick + 1);
        return map;
    }

    public Object getMajorityAnswer() {
        Map<String, Integer> answers = new HashMap<>();
        for (PlayerController player : users.values()) {
            Object answer = player.getAnswer();
            if (answer != null) {
                answers.put(answer.toString(), answers.getOrDefault(answer.toString(), 0) + 1);
            }
        }
        return answers.entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
    }

    public void setState() {
        updateStateRequest = true;
    }

    public void forceSetState() {
        this.eventPublisher.publishEvent(new DirtyStateScreen(instance));
    }

    public boolean haveStateUpdateRequest() {
        return updateStateRequest;
    }

    public List<PlayerController> getPlayers() {
        return List.copyOf(users.values());
    }

    public boolean haveGoTo() {
        return goToAction != null;
    }

    public StoryStep getGoToStep() {
        return getAllSteps().stream().filter((StoryStep item) -> Objects.equals(item.getSlug(), goToAction)).findFirst().orElse(null);
    }
}

