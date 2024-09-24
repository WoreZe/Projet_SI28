package fr.utc.multeract.server.scripts.htmlRenderer;

import java.util.Map;

abstract public class HtmlRenderer<BAG> {

    protected BAG bag;
    protected String template;

    public HtmlRenderer() {
        bag = initializeBag();
    }


    abstract protected BAG initializeBag();

    abstract public void setBinding(String name, Object value);
    abstract public void clearBinding();

    abstract public String render() throws Exception;

    public void setValues(Map<String, Object> bag) {
        clearBinding();
        for (Map.Entry<String, Object> entry : bag.entrySet()) {
            setBinding(entry.getKey(), entry.getValue());
        }
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public BAG getBag() {
        return bag;
    }

    public void setBag(BAG bag) {
        this.bag = bag;
    }
}
