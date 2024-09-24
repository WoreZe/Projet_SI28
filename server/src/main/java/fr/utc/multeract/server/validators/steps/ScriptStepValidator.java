package fr.utc.multeract.server.validators.steps;

import fr.utc.multeract.server.models.StoryStep;

public class ScriptStepValidator extends StepValidator {
    public ScriptStepValidator(StoryStep step) {
        super(step);
    }

    Object result;

    @Override
    public boolean validate(Object choice) {
        result = choice;
        return false;
    }

    @Override
    public Object computeResult() {
        return result;
    }


}
