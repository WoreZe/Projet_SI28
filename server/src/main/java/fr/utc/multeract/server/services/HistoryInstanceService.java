package fr.utc.multeract.server.services;

import fr.utc.multeract.server.models.HistoryInstanceState;
import fr.utc.multeract.server.models.Story;
import fr.utc.multeract.server.models.StoryInstance;
import fr.utc.multeract.server.models.StoryStep;
import fr.utc.multeract.server.models.json.HistoryUser;
import fr.utc.multeract.server.repositories.HistoryInstanceRepository;
import fr.utc.multeract.server.scripts.ChangeStepType;
import fr.utc.multeract.server.scripts.HtmlRendererService;
import fr.utc.multeract.server.scripts.StepScriptService;
import fr.utc.multeract.server.websocket.events.HistoryInstanceStateMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.script.ScriptException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
public class HistoryInstanceService {
    protected static final Logger logger = LogManager.getLogger();
    @Value("${app.history.timeout.new:86400000}") //1 day
    private Long newHistoryTimeout;
    @Value("${app.history.timeout.answer:3600000}") //1 hour
    private Long answerHistoryTimeout;
    @Autowired
    private HistoryInstanceRepository historyInstanceRepository;
    @Autowired
    private StepScriptService stepScriptService;
    @Autowired
    private HtmlRendererService htmlRendererService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    public StoryInstance generateHistoryInstance(Story history) {
        StoryInstance instance = new StoryInstance();
        instance.setHistory(history);
        instance.setCurrentStep(history.getEntrypoint());
        this.historyInstanceRepository.save(instance);
        return instance;
    }

    public Optional<StoryInstance> getHistoryInstance(Long id) {
        return this.historyInstanceRepository.findById(id);
    }

    public void setAnswer(StoryInstance instance, String historyUserName, Object answer) {
        instance.getUsers().get(historyUserName).setCurrentResponseValue(answer);
        this.historyInstanceRepository.save(instance);
        eventPublisher.publishEvent(new HistoryInstanceStateMessage(instance));
    }

    public Map<String, Object> getStatusOf(StoryInstance instance) {
        StoryStep currentStep = instance.getCurrentStep();
        //return step: and a answer map of 'username' -> 'answer'
        return Map.of("step", currentStep, "answers", instance.getUsers().entrySet().stream().collect(Map::of, (map, entry) -> map.put(entry.getKey(), entry.getValue().getCurrentResponse().getValue()), Map::putAll));
    }

    //every 1seconds
    //get all instances with state in-game
    //for each instance
    //  check if all user have answered
    //  if yes
    //    do routine (TODO)
    //  else
    //   if nobody answer since 2 hours, set HistoryInstanceState.TIMEOUT
    // end
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();

    @Scheduled(fixedRate = 1000)
    public void checkInstances() throws InterruptedException {
        List<StoryInstance> instances = this.historyInstanceRepository.findAllByStateIs(HistoryInstanceState.IN_GAME);
        long start = System.currentTimeMillis();
        var res = executor.invokeAll(instances.stream().map((StoryInstance instance) -> (Callable<Void>) () -> {
            executeRoutineFor(instance);
            return null;
        }).collect(Collectors.toList()));
        if (!instances.isEmpty()) {
            logger.info("Check " + instances.size() + " instance(s) took " + (System.currentTimeMillis() - start) + "ms");
        }
    }

    private void executeRoutineFor(StoryInstance instance) {
        boolean everyBodyAnswered = instance.hasUsers() && instance.getUsers().values().stream().allMatch(HistoryUser::hasResponseValue);
        stepScriptService.onTickHook(instance);
        if (everyBodyAnswered || instance.getCurrentStep().hasTimeLimit()) {
            Date stepEnterAt = instance.getEnterAt();
            ChangeStepType changeStepType = ChangeStepType.USER;
            if (instance.getCurrentStep().hasTimeLimit() && (System.currentTimeMillis() - stepEnterAt.getTime()) > instance.getCurrentStep().getTimeLimit()) {
                changeStepType = ChangeStepType.TIMEOUT;
            }
            stepScriptService.playEndStepScript(instance, changeStepType);
        }
        if (instance.getCreatedAt().getTime() + newHistoryTimeout < System.currentTimeMillis()) {
            instance.setState(HistoryInstanceState.TIMEOUT);
            this.historyInstanceRepository.save(instance);
        }
    }

    public String getHistoryInstancePage(StoryInstance instance) {
        String p  = htmlRendererService.renderStep(instance.getCurrentStep(), instance.getViewCache());
        if(p == null){
            return "No page found";
        }
        return p;
    }

    public void playHistoryInstance(StoryInstance history) throws ScriptException {
        history.setState(HistoryInstanceState.IN_GAME);
        history.setEnterAt(new Date());
        stepScriptService.beforeStepHook(history, history.getCurrentStep());
        this.historyInstanceRepository.save(history);
    }

    public Map<String, HistoryUser> getPredefinedUsers(String code) {
        var storyInstance = historyInstanceRepository.findByJoinCode(code);
        if(storyInstance.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Unable to find history instance");
        }
        Map<String, HistoryUser> u = storyInstance.get().getHistory().getPredefinedUsers();
        if(u.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NO_CONTENT);
        }
        return u;
    }
}
