package fr.utc.multeract.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import fr.utc.multeract.server.converter.HashMapConverter;
import fr.utc.multeract.server.validators.steps.StepValidator;
import jakarta.persistence.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"slug", "history_id"}))
public class StoryStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String slug;
    private String title;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    private HtmlPageElement page;

    @ManyToOne
    private Story history;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChoiceStepType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChoiceStepValidatorType validatorType;

    @Column(columnDefinition = "TEXT")
    private String scriptController;

    private Date updatedAt;

    private long timeLimit;

    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = ScriptHooks.class, fetch = FetchType.EAGER)
    private Set<ScriptHooks> hooks;

    @Convert(converter = HashMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> answers;

    public long getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(long timeLimit) {
        this.timeLimit = timeLimit;
    }

    public boolean hasTimeLimit() {
        return timeLimit > 0;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public HtmlPageElement getPage() {
        return page;
    }

    public void setPage(HtmlPageElement page) {
        this.page = page;
    }

    public Story getHistory() {
        return history;
    }

    public void setHistory(Story history) {
        this.history = history;
    }

    public String getScriptController() {
        return scriptController;
    }

    public void setScriptController(String scriptController) {
        this.scriptController = scriptController;
        this.updatedAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    @Transient
    public StepValidator getValidator() {
        return StepValidator.getValidator(this);
    }

    public ChoiceStepType getType() {
        return type;
    }

    public void setType(ChoiceStepType type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean containHook(ScriptHooks hook) {
        return hooks.contains(hook);
    }


    public Map<String, Object> toJsonData(boolean all) {
        Map<String, Object> json = new HashMap<>();
        json.put("id", id);
        json.put("slug", slug);
        json.put("title", title);
        json.put("type", type);
        json.put("updatedAt", updatedAt);
        json.put("timeLimit", timeLimit);
        json.put("answers", answers);
        if (all) {
            json.put("validator_type", validatorType);
            json.put("page", page);
            json.put("scriptController", scriptController);
        }
        return json;
    }

    @JsonValue
    public Map<String, Object> toJson() {
        return toJsonData(false);
    }

    public ChoiceStepValidatorType getValidatorType() {
        return validatorType;
    }

    public void setValidatorType(ChoiceStepValidatorType option) {
        this.validatorType = option;
    }
}
