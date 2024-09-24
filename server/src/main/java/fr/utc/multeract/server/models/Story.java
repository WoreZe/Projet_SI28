package fr.utc.multeract.server.models;

import fr.utc.multeract.server.converter.UserHistoryInstanceConverter;
import fr.utc.multeract.server.models.json.HistoryUser;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.context.annotation.Lazy;

import java.util.Map;

@Entity
public class Story {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lazy
    @OneToOne
    private StoryStep entrypoint;

    @Lazy
    @ManyToOne
    private User creator;

    private String title;

    @Column(length = 1000)
    private String description;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    private HistoryVisibility visibility;

    public Map<String, HistoryUser> getPredefinedUsers() {
        return predefinedUsers;
    }

    public void setPredefinedUsers(Map<String, HistoryUser> predefinedUsers) {
        this.predefinedUsers = predefinedUsers;
    }

    @Convert(converter = UserHistoryInstanceConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, HistoryUser> predefinedUsers;

    @ColumnDefault("2")
    private int maxPlayers;

    @ColumnDefault("1")
    private int minPlayers;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public StoryStep getEntrypoint() {
        return entrypoint;
    }

    public void setEntrypoint(StoryStep entrypoint) {
        this.entrypoint = entrypoint;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public HistoryVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(HistoryVisibility visibility) {
        this.visibility = visibility;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) {
        this.minPlayers = minPlayers;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
