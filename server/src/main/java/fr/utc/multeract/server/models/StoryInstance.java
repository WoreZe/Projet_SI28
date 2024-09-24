package fr.utc.multeract.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.utc.multeract.server.converter.HashMapConverter;
import fr.utc.multeract.server.converter.UserHistoryInstanceConverter;
import fr.utc.multeract.server.models.json.HistoryUser;
import fr.utc.multeract.server.utils.StringGenerator;
import jakarta.persistence.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Entity
public class StoryInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    private Story history;

    @ManyToOne(fetch = FetchType.EAGER)
    private StoryStep currentStep;

    @Enumerated(EnumType.STRING)
    private HistoryInstanceState state;

    @Column(columnDefinition = "char(8)", name = "join_code", unique = true, nullable = false)
    private String joinCode;

    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> pathsInfos;

    //this field store temp user for the history <username,<UserData>> and in user data, current user answser
    @Convert(converter = UserHistoryInstanceConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, HistoryUser> users;

    @ManyToOne(fetch = FetchType.EAGER)
    private User gameOwner;

    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> variables;

    private Date createdAt, enterAt;

    public StoryInstance() {
        users = new HashMap<>();
        variables = new HashMap<>();
        pathsInfos = new HashMap<>();
        state = HistoryInstanceState.WAITING_FOR_PLAYERS;
        joinCode = StringGenerator.generateCode();
        createdAt = Calendar.getInstance().getTime();
    }

    public Long getId() {
        return id;
    }

    public Story getHistory() {
        return history;
    }

    public void setHistory(Story history) {
        this.history = history;
    }

    public StoryStep getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(StoryStep currentStep) {
        this.currentStep = currentStep;
    }

    public String getJoinCode() {
        return joinCode;
    }

    public void setJoinCode(String joinCode) {
        this.joinCode = joinCode;
    }

    public Map<String, Object> getPathsInfos() {
        return pathsInfos;
    }

    public void setPathsInfos(Map<String, Object> pathsInfos) {
        this.pathsInfos = pathsInfos;
    }

    public HistoryInstanceState getState() {
        return state;
    }

    public void setState(HistoryInstanceState state) {
        this.state = state;
    }

    public Map<String, HistoryUser> getUsers() {
        return users;
    }

    //add user
    public void addUser(HistoryUser user) {
        this.users.put(user.getUsername(), user);
    }

    public User getGameOwner() {
        return gameOwner;
    }

    public void setGameOwner(User gameOwner) {
        this.gameOwner = gameOwner;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    @JsonIgnore
    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    @Transient
    public boolean hasUsers() {
        return !users.values().isEmpty();
    }

    public void flushViewCache() {
        variables.put("vCache", new HashMap<>());
    }

    @JsonIgnore
    public Date getEnterAt() {
        return enterAt;
    }

    public void setEnterAt(Date enterAt) {
        this.enterAt = enterAt;
    }

    public void incrementTickTimer() {
        variables.put("tickTimer", (int) variables.getOrDefault("tickTimer", 0) + 1);
    }

    @JsonIgnore
    public int getTickTimer() {
        return (int) variables.getOrDefault("tickTimer", 0);
    }
    @JsonIgnore
    public Map<String, Object> getViewCache() {
        return (Map<String, Object>) variables.getOrDefault("vCache", new HashMap<>());
    }

    public void clearUsersAnswers() {
        users.values().forEach(HistoryUser::clearResponse);
    }
}
