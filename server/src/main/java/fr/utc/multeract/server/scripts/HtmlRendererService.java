package fr.utc.multeract.server.scripts;

import fr.utc.multeract.server.models.StoryStep;
import fr.utc.multeract.server.models.HtmlPageElement;
import fr.utc.multeract.server.scripts.htmlRenderer.FreemarkerHtmlRenderer;
import fr.utc.multeract.server.scripts.htmlRenderer.HtmlRenderer;
import fr.utc.multeract.server.scripts.htmlRenderer.MarkupGroovyTemplateEngine;
import fr.utc.multeract.server.scripts.htmlRenderer.SimpleGroovyTemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HtmlRendererService {


    private static final Logger log = LoggerFactory.getLogger(HtmlRendererService.class);

    public String renderHtml(HtmlRenderer<?> renderer, String templateToRender, Map<String, Object> bag) {
        renderer.setTemplate(templateToRender);
        renderer.setValues(bag);
        try {
            return renderer.render();
        } catch (Exception e) {
            return null;
        }
    }

    public String renderHtml(HtmlRenderer<?> renderer, String templateToRender) {
        renderer.setTemplate(templateToRender);
        try {
            return renderer.render();
        } catch (Exception e) {
            return null;
        }
    }

    public String renderHtml(HtmlRendererType rendererType, String templateToRender, Map<String, Object> bag) {
        HtmlRenderer<?> renderer = getHtmlRenderer(rendererType);
        return renderHtml(renderer, templateToRender, bag);
    }

    public String renderHtml(HtmlRendererType rendererType, String templateToRender) {
        HtmlRenderer<?> renderer = getHtmlRenderer(rendererType);
        return renderHtml(renderer, templateToRender);
    }

    public String renderHtml(String rendererType, String templateToRender, Map<String, Object> bag) {
        HtmlRenderer<?> renderer = getHtmlRenderer(rendererType);
        return renderHtml(renderer, templateToRender, bag);
    }

    public String renderHtml(String rendererType, String templateToRender) {
        HtmlRenderer<?> renderer = getHtmlRenderer(rendererType);
        return renderHtml(renderer, templateToRender);
    }

    public HtmlRenderer<?> getHtmlRenderer(HtmlRendererType rendererType) {
        return switch (rendererType) {
            case MARKUP_GROOVY -> new MarkupGroovyTemplateEngine();
            case FREEMARKER -> new FreemarkerHtmlRenderer();
            case SIMPLE_GROOVY -> new SimpleGroovyTemplateEngine();
        };
    }

    public HtmlRenderer<?> getHtmlRenderer(String rendererType) {
        return switch (rendererType) {
            case "markup-groovy" -> new MarkupGroovyTemplateEngine();
            case "freemarker" -> new FreemarkerHtmlRenderer();
            case "simple-groovy" -> new SimpleGroovyTemplateEngine();
            default -> throw new IllegalArgumentException("Invalid renderer type: " + rendererType);
        };
    }

    public String renderStep(StoryStep step, Map<String, Object> bag){
        return renderHtmlElement(step.getPage(), bag);
    }

    public String renderHtmlElement(HtmlPageElement element, Map<String, Object> bag){
        return renderHtml(element.getRendererType(), element.getContent(), bag);
    }
}
