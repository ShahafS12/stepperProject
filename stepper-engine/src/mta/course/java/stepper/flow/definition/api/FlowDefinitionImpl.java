package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.STFlow;
import dataloader.generated.STStepsInFlow;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
import java.util.List;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<StepUsageDeclaration> steps;

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
    }

    public FlowDefinitionImpl(STFlow stFlow)
    {
        this.name = stFlow.getName();
        this.description = stFlow.getSTFlowDescription();
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        STStepsInFlow stStepsInFlow = stFlow.getSTStepsInFlow();
        for(int i = 0; i < stStepsInFlow.getSTStepInFlow().size(); i++)
        {
            steps.add(new StepUsageDeclarationImpl(stStepsInFlow.getSTStepInFlow().get(i)));
        }
    }

    @Override
    public void addFlowOutput(String outputName) {
        flowOutputs.add(outputName);
    }

    @Override
    public void addFlowStep(StepUsageDeclaration stepUsageDeclaration) {
        steps.add(stepUsageDeclaration);
    }

    @Override
    public void validateFlowStructure() {
        // do some validation logic...
        //TODO: implement
    }

    @Override
    public List<DataDefinitionDeclaration> getFlowFreeInputs() {
        return new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public List<StepUsageDeclaration> getFlowSteps() {
        return steps;
    }

    @Override
    public List<String> getFlowFormalOutputs() {
        return flowOutputs;
    }
}
