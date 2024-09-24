package fr.utc.multeract.server.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fr.utc.multeract.server.scripts.HtmlRendererType;
import jakarta.persistence.*;

@Entity
@JsonIgnoreProperties
public class HtmlPageElement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private HtmlRendererType rendererType;

    @Column(columnDefinition = "TEXT")
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public HtmlRendererType getRendererType() {
        return rendererType;
    }

    public void setRendererType(HtmlRendererType rendererType) {
        this.rendererType = rendererType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
