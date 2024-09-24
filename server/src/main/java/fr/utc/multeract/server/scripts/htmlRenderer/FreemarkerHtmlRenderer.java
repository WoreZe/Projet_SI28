package fr.utc.multeract.server.scripts.htmlRenderer;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

public class FreemarkerHtmlRenderer extends HtmlRenderer<Map<String, Object>>{
    public FreemarkerHtmlRenderer() {
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
        Template t = new Template("template", new StringReader(template), prepareConfiguration());
        Writer out = new StringWriter();
        t.process(bag, out);
        return out.toString();
    }

    private Configuration prepareConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_23);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        return configuration;
    }
}
