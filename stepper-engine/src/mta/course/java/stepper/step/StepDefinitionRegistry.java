package mta.course.java.stepper.step;

import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.impl.HelloWorldStep;
import mta.course.java.stepper.step.impl.PersonDetailsStep;
import mta.course.java.stepper.step.impl.SpendSomeTimeStep.SpendSomeTimeStep;
import mta.course.java.stepper.step.impl.CollectFilesInFolderStep.CollectFilesInFolderStep;

public enum StepDefinitionRegistry {
    HELLO_WORLD(new HelloWorldStep()),
    PERSON_DETAILS(new PersonDetailsStep()),
    COLLECT_FILES_IN_FOLDER(new CollectFilesInFolderStep()),
    SPEND_SOME_TIME(new SpendSomeTimeStep())
    ;

    private final StepDefinition stepDefinition;

    StepDefinitionRegistry(StepDefinition stepDefinition) {
        this.stepDefinition = stepDefinition;
    }


    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    public static StepDefinitionRegistry fromString(String name) {
        name = name.replaceAll(" ", "_");
        for (StepDefinitionRegistry registry : values()) {
            if (registry.name().equalsIgnoreCase(name)) {
                return registry;
            }
        }
        return null;
    }
}
