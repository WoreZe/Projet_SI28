package fr.utc.multeract.server.validators.steps;

import fr.utc.multeract.server.models.StoryStep;

public class NoValidator extends StepValidator {
    public NoValidator(StoryStep step) {
        super(step);
    }

    Object choice;

    @Override
    public boolean validate(Object choice) {
        return true;
    }

    @Override
    public Object computeResult() {
        return choice;
    }
}
