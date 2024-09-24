package fr.utc.multeract.server.scripts.htmlRenderer;

import groovy.text.TemplateEngine;

public class MarkupGroovyTemplateEngine extends GroovyTemplateEngine {
    public MarkupGroovyTemplateEngine() {
        super();
    }

    @Override
    protected TemplateEngine getTemplateEngine() {
        return new groovy.text.markup.MarkupTemplateEngine();
    }
}