package fr.utc.multeract.server.scripts.htmlRenderer;

import groovy.lang.Writable;
import groovy.text.TemplateEngine;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

abstract public class GroovyTemplateEngine extends HtmlRenderer<Map<String, Object>>{
    public GroovyTemplateEngine() {
        super();
    }

    @Override
    protected Map<String, Object> initializeBag() {
        return new HashMap<>();
    }

    @Override
    public void setBinding(String name, Object value) {
        bag.put(name, value);
    }

    @Override
    public void clearBinding() {
        bag.clear();
    }

    @Override
    public String render() throws Exception {
        Writable writable = getTemplateEngine().createTemplate(template).make(bag);
        StringWriter writer = new StringWriter();
        writable.writeTo(writer);
        return writer.toString();
    }

    abstract protected TemplateEngine getTemplateEngine();
}
