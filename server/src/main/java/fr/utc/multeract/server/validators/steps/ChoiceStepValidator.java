package fr.utc.multeract.server.validators.steps;

import fr.utc.multeract.server.models.StoryStep;

public class ChoiceStepValidator extends StepValidator {


    public ChoiceStepValidator(StoryStep step) {
        super(step);
    }

    @Override
    public boolean validate(Object choice) {
        try{
            Integer.parseInt(choice.toString());
            return true;
        } catch (NumberFormatException e){
            return false;
        }
    }

    @Override
    public Object computeResult() {
        return null;
    }
}
