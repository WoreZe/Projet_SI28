package fr.utc.multeract.server.scripts;

public enum HtmlRendererType {
    MARKUP_GROOVY("markup-groovy"),
    FREEMARKER("freemarker"),
    SIMPLE_GROOVY("simple-groovy");

    private final String type;

    HtmlRendererType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
