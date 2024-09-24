package fr.utc.multeract.server.endpoints;

import fr.utc.multeract.server.models.HtmlPageElement;
import fr.utc.multeract.server.models.Story;
import fr.utc.multeract.server.models.StoryStep;
import fr.utc.multeract.server.scripts.HtmlRendererType;
import fr.utc.multeract.server.services.HistorySpecService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/history/{id}", produces = "application/json")
public class HistorySpecEndpoints {
    private static final Logger log = LoggerFactory.getLogger(HistorySpecEndpoints.class);
    @Autowired
    HistorySpecService historySpecService;

    @GetMapping(path = "")
    public Story getHistory(@PathVariable("id") String id) {
        return historySpecService.getHistory(Long.valueOf(id)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history"));
    }

    @GetMapping(path = "/steps")
    public List<Map<String, Object>> getHistorySteps(@PathVariable("id") String id) {
        return historySpecService.getHistorySteps(Long.valueOf(id)).stream().map(step -> {
            step.setHistory(null);
            return step.toJsonData(true);
        }).toList();
    }

    @PostMapping(path = "/steps")
    public StoryStep createHistoryStep(@PathVariable("id") String id, @RequestBody StoryStep step) {
        return historySpecService.createHistoryStep(Long.valueOf(id), step);
    }

    @GetMapping(path = "/steps/{stepId}")
    public Map<?, ?> getHistoryStep(@PathVariable("id") String id, @PathVariable("stepId") String stepId) {
        return historySpecService.getHistoryStep(Long.valueOf(id), Long.valueOf(stepId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step")).toJsonData(true);
    }

    @PostMapping(path = "/steps/{stepId}/run")
    public Object runScript(@PathVariable("id") String id, @PathVariable("stepId") String stepId) {
        var step = historySpecService.getHistoryStep(Long.valueOf(id), Long.valueOf(stepId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step"));
        return historySpecService.runScriptAsTest(step, new HashMap<>());
    }

    @PutMapping(path = "/steps/{stepId}")
    public Object updateHistoryStep(@PathVariable("id") String id, @PathVariable("stepId") String stepId, @RequestBody StoryStep step) {
        if (!step.getId().equals(Long.valueOf(stepId))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Step id does not match the path");
        }
        return historySpecService.updateHistoryStep(Long.valueOf(id), step).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step")).toJsonData(true);
    }

    @DeleteMapping(path = "/steps/{stepId}")
    public void deleteHistoryStep(@PathVariable("id") String id, @PathVariable("stepId") String stepId) {
        historySpecService.deleteHistoryStep(Long.valueOf(id), Long.valueOf(stepId));
    }

    @PutMapping(path = "/steps/{stepId}/script")
    public Object updateHistoryStepScript(@PathVariable("id") String id, @PathVariable("stepId") String stepId, @RequestBody Map<String, Object> data) {
        var step = historySpecService.getHistoryStep(Long.valueOf(id), Long.valueOf(stepId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step"));
        step.setScriptController(data.get("script").toString());
        return historySpecService.updateHistoryStep(Long.valueOf(id), step).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step")).toJsonData(true);
    }

    @PutMapping(path = "/steps/{stepId}/template")
    public Object updateHistoryStepHtmlPage(@PathVariable("id") String id, @PathVariable("stepId") String stepId, @RequestBody Map<String, Object> data) {
        var step = historySpecService.getHistoryStep(Long.valueOf(id), Long.valueOf(stepId)).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step"));
        var renderer = data.getOrDefault("renderer", HtmlRendererType.FREEMARKER.name());
        var html = data.get("template").toString();
        if (renderer == null || html == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing renderer or template");
        }
        try {
            renderer = HtmlRendererType.valueOf(renderer.toString());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid renderer");
        }
        if (step.getPage() == null) {
            step.setPage(new HtmlPageElement());
        }
        step.getPage().setRendererType((HtmlRendererType) renderer);
        step.getPage().setContent(html);
        return historySpecService.updateHistoryStep(Long.valueOf(id), step).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find history step")).toJsonData(true);
    }
}
