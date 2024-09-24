package fr.utc.multeract.server;

import fr.utc.multeract.server.scripts.ScriptLogger;
import org.openjdk.nashorn.api.scripting.ScriptObjectMirror;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;

public class MainTestJsEngine {

    private static final Logger log = LoggerFactory.getLogger(MainTestJsEngine.class);

    public static void main(String[] args) throws ScriptException, NoSuchMethodException {
        //preparer le moteur
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("nashorn");
        Compilable jsCompilable = (Compilable) engine;
        Invocable invocable = (Invocable) engine;
        ScriptContext scriptCtxt = engine.getContext();
        engine.setContext(scriptCtxt);


        //compile the script
        CompiledScript jsScript = jsCompilable.compile("""
                function onBeforeStep() {
                    //controller.getRandomPlayer().vibrate();
                    console.log("Hello from Nashorn");
                }
                """);

        Long startTime = System.currentTimeMillis();

        //set bindings and render....
        Bindings engineScope = scriptCtxt.getBindings(ScriptContext.ENGINE_SCOPE);
        ScriptLogger logger = new ScriptLogger(log);
        engineScope.put("console", logger);
        engineScope.put("controller","");
        jsScript.eval(engineScope);
        //check if function test exist
        if(engine.get("onBeforeStep") != null){
            invocable.invokeFunction("onBeforeStep");
        }
        invocable.invokeFunction("onBeforeStep", "Logged from inside Nashorn by an outside function call in an external logger");
        log.info("Execution time: " + (System.currentTimeMillis() - startTime) + "ms");
    }

}