package fr.utc.multeract.server.validators.steps;

import fr.utc.multeract.server.models.StoryStep;

abstract public class StepValidator {
    public static StepValidator getValidator(StoryStep step) {
        switch (step.getValidatorType()) {
            case CHOICE_IN_LIST_VALIDATOR -> {
                return new ChoiceStepValidator(step);
            }
            case SCRIPTED_VALIDATOR -> {
                return new ScriptStepValidator(step);
            }
            case NO_VALIDATOR -> {
                return new NoValidator(step);
            }
            default -> {
                throw new IllegalArgumentException("Unknown step type");
            }
        }
    }

    protected final StoryStep step;

    public StepValidator(StoryStep step) {
        this.step = step;
    }

    abstract public boolean validate(Object choice);

    abstract public Object computeResult();
}
