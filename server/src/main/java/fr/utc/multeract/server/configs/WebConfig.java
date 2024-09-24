package fr.utc.multeract.server.configs;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Configuration
public class WebConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL).registerModule(new Hibernate6Module());
    }

    @Bean
    public ScriptEngine nashornScriptEngine() {
        return new NashornScriptEngineFactory().getScriptEngine("--language=es6");
    }
}
