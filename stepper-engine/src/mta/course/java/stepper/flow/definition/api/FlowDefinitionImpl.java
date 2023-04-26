package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.STFlow;
import dataloader.generated.STFlowLevelAlias;
import dataloader.generated.STFlowLevelAliasing;
import dataloader.generated.STStepsInFlow;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<StepUsageDeclaration> steps;

    private final Map<Map<String,String>,String>  flowLevelAlias;

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
    }

    public FlowDefinitionImpl(STFlow stFlow)
    {
        this.name = stFlow.getName();
        this.description = stFlow.getSTFlowDescription();
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        STStepsInFlow stStepsInFlow = stFlow.getSTStepsInFlow();
        for(int i = 0; i < stStepsInFlow.getSTStepInFlow().size(); i++)
        {
            steps.add(new StepUsageDeclarationImpl(stStepsInFlow.getSTStepInFlow().get(i)));
        }
        STFlowLevelAliasing stFlowLevelAliasing = stFlow.getSTFlowLevelAliasing();
        List<STFlowLevelAlias> stFlowLevelAliasList = stFlowLevelAliasing.getSTFlowLevelAlias();
        for(STFlowLevelAlias stFlowLevelAlias : stFlowLevelAliasList)
        {
            Map<String,String> map = new HashMap<>();
            map.put(stFlowLevelAlias.getStep(),stFlowLevelAlias.getSourceDataName());
            flowLevelAlias.put(map,stFlowLevelAlias.getAlias());
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
