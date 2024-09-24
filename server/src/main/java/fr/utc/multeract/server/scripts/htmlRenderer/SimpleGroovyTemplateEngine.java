package fr.utc.multeract.server.scripts.htmlRenderer;

import groovy.text.TemplateEngine;

public class SimpleGroovyTemplateEngine extends GroovyTemplateEngine {
    public SimpleGroovyTemplateEngine() {
        super();
    }

    @Override
    protected TemplateEngine getTemplateEngine() {
        return new groovy.text.SimpleTemplateEngine();
    }
}
