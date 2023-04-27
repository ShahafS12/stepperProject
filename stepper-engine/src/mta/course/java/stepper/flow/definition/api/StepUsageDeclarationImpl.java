package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.STStepInFlow;
import mta.course.java.stepper.step.StepDefinitionRegistry;
import mta.course.java.stepper.step.api.StepDefinition;

public class StepUsageDeclarationImpl implements StepUsageDeclaration {
    private final StepDefinition stepDefinition;
    private final boolean skipIfFail;
    private final String stepName;
    private final String stepAlias;

    public StepUsageDeclarationImpl(StepDefinition stepDefinition) {
        this(stepDefinition, false, stepDefinition.name());
    }

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, String name) {
        this(stepDefinition, false, name);
    }

    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
        this.stepAlias = stepName;
    }
    public StepUsageDeclarationImpl(StepDefinition stepDefinition, boolean skipIfFail, String stepName, String stepAlias) {
        this.stepDefinition = stepDefinition;
        this.skipIfFail = skipIfFail;
        this.stepName = stepName;
        this.stepAlias = stepAlias;
    }

    public StepUsageDeclarationImpl(STStepInFlow stStepInFlow)
    {
        this.skipIfFail = stStepInFlow.isContinueIfFailing()==null;
        this.stepName = stStepInFlow.getName();
        StepDefinitionRegistry stepDefinitionRegistry = StepDefinitionRegistry.fromString(stStepInFlow.getName());
        stepDefinition = stepDefinitionRegistry.getStepDefinition();
        if(stStepInFlow.getAlias() == null)
            stepAlias = stepName;
        else
        stepAlias = stStepInFlow.getAlias();
    }

    @Override
    public String getFinalStepName() {
        return stepAlias;
    }

    @Override
    public StepDefinition getStepDefinition() {
        return stepDefinition;
    }

    @Override
    public boolean skipIfFail() {
        return skipIfFail;
    }
}
