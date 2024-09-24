package fr.utc.multeract.server.endpoints;

import fr.utc.multeract.server.models.HistoryVisibility;
import fr.utc.multeract.server.models.Story;
import fr.utc.multeract.server.models.StoryStep;
import fr.utc.multeract.server.services.HistorySpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(value = "/history/", produces = "application/json")
@ResponseBody
public class HistoryEndpoints {
    @Autowired
    HistorySpecService historySpecService;

    @GetMapping(path = "")
    public List<Story> getHistories() {
        return historySpecService.getHistories(HistoryVisibility.PUBLIC);
    }
}
