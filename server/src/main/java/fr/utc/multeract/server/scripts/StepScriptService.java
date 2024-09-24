package fr.utc.multeract.server.scripts;

import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.models.StoryStep;
import fr.utc.multeract.server.repositories.HistoryInstanceRepository;
import fr.utc.multeract.server.repositories.StoryStepRepository;
import fr.utc.multeract.server.scripts.stories.PlayerController;
import fr.utc.multeract.server.scripts.stories.StoryPlayer;
import fr.utc.multeract.server.websocket.events.DirtyStateScreen;
import fr.utc.multeract.server.websocket.events.HistoryInstanceStateMessage;
import fr.utc.multeract.server.websocket.events.PlayerActionListEvent;
import org.openjdk.nashorn.api.scripting.NashornScriptEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StepScriptService {
    private static final Logger log = LoggerFactory.getLogger(StepScriptService.class);
    Map<Long, StoryCompiledScript> _cache = new HashMap<>();

    @Autowired
    private StoryStepRepository storyStepRepository;

    @Autowired
    private HistoryInstanceRepository historyInstanceRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private ScriptEngine getFreshEngine() {
        return new NashornScriptEngineFactory().getScriptEngine("--language=es6");
    }

    private ScriptContext getScripContext(StoryCompiledScript compiledScript, StoryStep step) {
        ScriptContext scriptCtxt = compiledScript.cScript.getEngine().getContext();
        Bindings engineScope = scriptCtxt.getBindings(ScriptContext.ENGINE_SCOPE);
        Logger logger = LoggerFactory.getLogger(step.getId() + " - " + step.getSlug());
        engineScope.put("console", new ScriptLogger(logger));
        return scriptCtxt;
    }

    private CompiledScript compile(String script) throws Exception {
        return ((Compilable) getFreshEngine()).compile(script);
    }

    public void runFunctionIfExit(StoryInstance instance, String name, List<Object> args) throws ScriptException {
        StoryCompiledScript compiledScript = getCompiledScript(instance.getCurrentStep());
        Bindings engineScope = getScripContext(compiledScript, instance.getCurrentStep()).getBindings(ScriptContext.ENGINE_SCOPE);
        var player = new StoryPlayer(instance, storyStepRepository, eventPublisher);
        engineScope.put("controller", player);
        compiledScript.cScript.eval(engineScope);
        //check if function test exist
        StoryStep beforeStep = instance.getCurrentStep();
        var isRanWell = this.runIfExist(name, compiledScript, args.toArray());
        if (isRanWell == null) {
            log.error("Error while running function {}", name);
            return;
        }
        if (isRanWell) {
            validateAndSave(instance, player);
            if (player.haveGoTo()) {
                //flush view parameters
                instance.flushViewCache();
                beforeStepHook(instance, beforeStep);
                player.setState();
            }
            if (player.haveStateUpdateRequest()) {
                this.eventPublisher.publishEvent(new DirtyStateScreen(instance));
            }
        }
    }

    private Object runFunctionWithReturnIfExist(StoryInstance instance, String name, List<Object> args) throws ScriptException {
        StoryCompiledScript compiledScript = getCompiledScript(instance.getCurrentStep());
        Bindings engineScope = getScripContext(compiledScript, instance.getCurrentStep()).getBindings(ScriptContext.ENGINE_SCOPE);
        var player = new StoryPlayer(instance, storyStepRepository, eventPublisher);
        engineScope.put("controller", player);
        compiledScript.cScript.eval(engineScope);
        //check if function test exist
        ExecutionReturn ret = this.runWithReturnIfExist(name, compiledScript.cScript.getEngine(), args.toArray());
        if(ret.isOk()){
            validateAndSave(instance, player);
            if (player.haveGoTo()) {
                //flush view parameters
                instance.flushViewCache();
                beforeStepHook(instance, instance.getCurrentStep());
                player.setState();
            }
            if (player.haveStateUpdateRequest()) {
                this.eventPublisher.publishEvent(new DirtyStateScreen(instance));
            }
        }
        return ret.value();
    }

    private ExecutionReturn runWithReturnIfExist(String name, ScriptEngine engine, Object[] array) {
        try {
            if (engine.get(name) != null) {
                var res = ((Invocable) engine).invokeFunction(name, array);
                return new ExecutionReturn(res, true);
            } else {
                return new ExecutionReturn(null, false);
            }
        } catch (Exception e) {
            log.error("Error while running function", e);
            return new ExecutionReturn(null, false);
        }
    }

    @Transactional
    protected void validateAndSave(StoryInstance instance, StoryPlayer player) throws ScriptException {
        //prepare instance state
        if (player.haveGoTo()) {
            if (player.getGoToStep() != null) {
                instance.setCurrentStep(player.getGoToStep());
            }
        }

        for (PlayerController controller : player.getPlayers()) {
            if (!controller.getPlayerActions().isEmpty())
                eventPublisher.publishEvent(new PlayerActionListEvent(instance, controller.getPlayerActions()));
        }
        instance.setVariables(player.toJsonStateSave());
        this.historyInstanceRepository.save(instance);
    }

    public void beforeStepHook(StoryInstance instance, StoryStep fromStep) throws ScriptException {
        instance.setEnterAt(new Date());
        instance.clearUsersAnswers();
        historyInstanceRepository.save(instance);
        runFunctionIfExit(instance, "onBeforeStep", List.of(fromStep));
        eventPublisher.publishEvent(new HistoryInstanceStateMessage(instance));
    }

    public void stepEndHook(StoryInstance instance, ChangeStepType type) throws ScriptException {
        runFunctionIfExit(instance, "onStepEnd", List.of(type));
        eventPublisher.publishEvent(new HistoryInstanceStateMessage(instance));
    }

    private Boolean runIfExist(String name, StoryCompiledScript script, Object[] array) {
        try {
            var engine = script.cScript.getEngine();
            if (engine.get(name) != null) {
                ((Invocable) engine).invokeFunction(name, array);
            } else {
                return false;
            }
        } catch (Exception e) {
            this.appendError(script.step.getId() + "-" + name, e);
            return null;
        }
        return true;
    }

    private void appendError(String name, Exception e) {
        //save in ./logs/steps/<name>.log
        var file = new java.io.File("./logs/steps/" + name + ".log");
        try {
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            var writer = new java.io.FileWriter(file);
            writer.write(e.getMessage());
            writer.close();
        } catch (Exception ex) {
            log.error("Error while writing error log", ex);
        }
    }

    public void onTickHook(StoryInstance instance) {
        try {
            runFunctionIfExit(instance, "onTick", List.of());
        } catch (ScriptException e) {
            log.error("Error while running before step hook", e);
        }
    }

    public StoryCompiledScript getCompiledScript(StoryStep step) {
        if (step.getScriptController() == null) {
            throw new IllegalArgumentException("Step script controller is null");
        }
        if (_cache.containsKey(step.getId())) {
            StoryCompiledScript cached = _cache.get(step.getId());
            Date upAt = step.getUpdatedAt();

            if (upAt != null && upAt.getTime() > cached.getCompiledAt()) {
                try {
                    CompiledScript cScript = compile(step.getScriptController());
                    _cache.put(step.getId(), new StoryCompiledScript(cScript, step));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                CompiledScript cScript = compile(step.getScriptController());
                _cache.put(step.getId(), new StoryCompiledScript(cScript, step));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return _cache.get(step.getId());
    }

    public void playEndStepScript(StoryInstance instance, ChangeStepType changeStepType) {
        try {
            stepEndHook(instance, changeStepType);
            instance.setEnterAt(new Date());
            //clear user answers
            instance.getUsers().values().forEach(user -> user.setCurrentResponse(null));
            //save instance
            this.historyInstanceRepository.save(instance);
        } catch (ScriptException e) {
            log.error("Error while running endStep step hook", e);
        }
    }

    public Object runValidator(StoryInstance instance, String historyUserName, Object answer) {
        try {
            return runFunctionWithReturnIfExist(instance, "validate", List.of(historyUserName, answer));
        } catch (ScriptException e) {
            log.error("Error while running validator", e);
        }
        return null;
    }

    public static class StoryCompiledScript {
        private final long compiledAt;
        private final CompiledScript cScript;
        private final StoryStep step;

        public StoryCompiledScript(CompiledScript cScript, StoryStep step) {
            this.cScript = cScript;
            this.step = step;
            this.compiledAt = System.currentTimeMillis();
        }

        public CompiledScript getScript() {
            return cScript;
        }

        public long getCompiledAt() {
            return compiledAt;
        }
    }

    private record ExecutionReturn(Object value, boolean isOk) {
    }
}
