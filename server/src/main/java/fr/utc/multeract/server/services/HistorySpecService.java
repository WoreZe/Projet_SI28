package fr.utc.multeract.server.services;

import fr.utc.multeract.server.models.*;
import fr.utc.multeract.server.models.json.HistoryUser;
import fr.utc.multeract.server.repositories.HistoryInstanceRepository;
import fr.utc.multeract.server.repositories.HistoryRepository;
import fr.utc.multeract.server.repositories.StoryStepRepository;
import fr.utc.multeract.server.scripts.ChangeStepType;
import fr.utc.multeract.server.scripts.StepScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class HistorySpecService {
    private static final Logger log = LoggerFactory.getLogger(HistorySpecService.class);
    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    StoryStepRepository historyStepRepository;

    @Autowired
    HistoryInstanceRepository historyInstanceRepository;

    @Autowired
    StepScriptService stepScriptService;

    public Optional<Story> getHistory(Long aLong) {
        return historyRepository.findById(aLong);
    }

    public List<StoryStep> getHistorySteps(Long aLong) {
        return historyStepRepository.findAllByHistoryId(aLong);
    }

    public StoryStep createHistoryStep(Long aLong, StoryStep step) {
        step.setHistory(historyRepository.findById(aLong).orElseThrow());
        return historyStepRepository.save(step);
    }

    public Optional<StoryStep> getHistoryStep(Long history, Long stepId) {
        Optional<StoryStep> step = historyStepRepository.findById(stepId);
        if (step.isPresent() && step.get().getHistory().getId().equals(history)) {
            return step;
        }
        return Optional.empty();
    }

    public Optional<StoryStep> updateHistoryStep(Long aLong, StoryStep step) {
        if (step.getHistory().getId().equals(aLong)) {
            return Optional.of(historyStepRepository.save(step));
        }
        return Optional.empty();
    }

    public void deleteHistoryStep(Long aLong, Long aLong1) {
        Optional<StoryStep> step = historyStepRepository.findById(aLong1);
        if (step.isPresent() && step.get().getHistory().getId().equals(aLong)) {
            historyStepRepository.delete(step.get());
        }
    }

    public List<Story> getHistories(HistoryVisibility visibility) {
        return historyRepository.findAllByVisibilityIs(visibility);
    }

    public StoryInstance runScriptAsTest(StoryStep step, Map<String, Object> userBag) {
        //find a HistoryInstance for the history with a status of TESTING
        var instance = historyInstanceRepository.findAllByStateIsAndHistory(HistoryInstanceState.TESTING, step.getHistory()).stream().findFirst().orElse(null);
        if (instance == null) {
            instance = new StoryInstance();
            instance.setHistory(step.getHistory());
            instance.setState(HistoryInstanceState.TESTING);
            for (int i = 0; i < step.getHistory().getMaxPlayers(); i++) {
                instance.addUser(new HistoryUser("", "bot" + i, "bot" + i + "-unique-id"));
            }
            instance = historyInstanceRepository.save(instance);
        }
        instance.setCurrentStep(step);
        this.stepScriptService.playEndStepScript(instance, ChangeStepType.TIMEOUT);
        return instance;
    }
}
