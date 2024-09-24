package fr.utc.multeract.server;

import com.github.javafaker.Faker;
import fr.utc.multeract.server.services.BootstrapService;
import org.apache.catalina.startup.Bootstrap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@SpringBootApplication
@EnableScheduling
public class ServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }

    @Autowired
    private BootstrapService booster;
    @EventListener
    public void onServerStarted(ApplicationStartedEvent event) {
        if(!booster.isBootstrapped()) {
            booster.bootstrap();
        }
    }
}
