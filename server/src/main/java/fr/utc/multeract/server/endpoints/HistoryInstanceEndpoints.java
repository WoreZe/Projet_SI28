package fr.utc.multeract.server.endpoints;

import fr.utc.multeract.server.models.*;
import fr.utc.multeract.server.models.json.HistoryUser;
import fr.utc.multeract.server.repositories.HistoryRepository;
import fr.utc.multeract.server.scripts.StepScriptService;
import fr.utc.multeract.server.services.HistoryInstanceService;
import fr.utc.multeract.server.services.PermissionManager;
import fr.utc.multeract.server.validators.steps.ScriptStepValidator;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping(value = "/instances", produces = "application/json", consumes = "application/json")
@ResponseBody
public class HistoryInstanceEndpoints {
    private static final Logger log = LoggerFactory.getLogger(HistoryInstanceEndpoints.class);
    @Autowired
    HistoryInstanceService historyInstanceService;

    @Autowired
    HistoryRepository historyRepository;

    @Autowired
    PermissionManager permissionManager;
    @Autowired
    private StepScriptService stepScriptService;

    @GetMapping(path = "/player")
    public Map<String, HistoryUser> getFromCodeHistoryPreDefinedPlayers(@RequestParam(name = "code") String code) {
        return historyInstanceService.getPredefinedUsers(code);
    }

    @PostMapping(path = "/create")
    public StoryInstance createHistoryInstance(@RequestBody Map<String, Object> payload) {
        if (!payload.containsKey("history")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing history id");
        }
        Long historyId = Long.valueOf((Integer) payload.get("history"));
        var history = historyRepository.findById(historyId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history"));
        //history is accessible by the creator or if history.visibility is public
        if (!history.getCreator().getUsername().equals(permissionManager.getAuthenticationClaims().get("user")) && history.getVisibility() != HistoryVisibility.PUBLIC) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to create an instance of this history");
        }
        return historyInstanceService.generateHistoryInstance(history);
    }

    @PutMapping(path = "/answer")
    @Transactional
    public StoryInstance answerHistoryInstance(@RequestBody Map<String, Object> payload) {
        Long instance_id = permissionManager.getInstanceId();
        if (!payload.containsKey("answer")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing answer");
        }
        var instance = historyInstanceService.getHistoryInstance(instance_id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history instance"));
        permissionManager.accessHistoryInstance(instance);
        var validator = instance.getCurrentStep().getValidator();
        if (validator instanceof ScriptStepValidator) {
            Object validated = stepScriptService.runValidator(instance, permissionManager.getHistoryUserName(), payload.get("answer"));
            validator.validate(validated);
            historyInstanceService.setAnswer(instance, permissionManager.getHistoryUserName(), validated);
            if (validator.computeResult() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid answer");
            }
        } else {
            if (!validator.validate(payload.get("answer"))) {
                historyInstanceService.setAnswer(instance, permissionManager.getHistoryUserName(), null);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid answer");
            }
        }
        historyInstanceService.setAnswer(instance, permissionManager.getHistoryUserName(), validator.computeResult());
        return instance;
    }
    @GetMapping(path = "/me")
    public StoryInstance getMyHistoryInstance() {
        var instance = historyInstanceService.getHistoryInstance(permissionManager.getInstanceId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history instance"));
        permissionManager.accessHistoryInstance(instance);
        return instance;
    }

    @GetMapping(path = "/{id}")
    public StoryInstance getHistoryInstance(@PathVariable("id") String id) {
        var history = historyInstanceService.getHistoryInstance(Long.valueOf(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history instance"));
        permissionManager.accessHistoryInstance(history);
        return history;
    }

    @PostMapping(path = "/{id}/play")
    public StoryInstance playHistoryInstance(@PathVariable("id") String id) {
        var history = historyInstanceService.getHistoryInstance(Long.valueOf(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history instance"));
        permissionManager.accessHistoryInstance(history);
        if (history.getState() != HistoryInstanceState.WAITING_FOR_PLAYERS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "History instance is not waiting for players");
        }
        try {
            historyInstanceService.playHistoryInstance(history);
            return history;
        } catch (Exception e) {
            log.error("Error while playing history instance", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while playing history instance");
        }
    }

    @GetMapping(path = "/{id}/step")
    @RolesAllowed(Roles.HISTORY_INSTANCE_USER)
    public StoryStep getCurrentStep(@PathVariable("id") String id) {
        var instance = historyInstanceService.getHistoryInstance(Long.valueOf(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history instance"));
        permissionManager.accessHistoryInstance(instance);
        //load the step and getAvailableSteps lazy
        return instance.getCurrentStep();
    }

    @GetMapping(path = "/{id}/page")
    public Map<String, Object> getHistoryInstancePage(@PathVariable("id") String id) {
        var instance = historyInstanceService.getHistoryInstance(Long.valueOf(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history instance"));
        permissionManager.accessHistoryInstance(instance);
        return Map.of("page", historyInstanceService.getHistoryInstancePage(instance));
    }
}
