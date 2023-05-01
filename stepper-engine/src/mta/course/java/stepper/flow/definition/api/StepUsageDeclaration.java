package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.step.api.StepDefinition;

public interface StepUsageDeclaration {
    String getFinalStepName();
    String getStepName();
    StepDefinition getStepDefinition();
    boolean skipIfFail();
    String getStepName();
}
