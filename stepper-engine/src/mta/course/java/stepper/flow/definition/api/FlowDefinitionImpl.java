package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.*;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.*;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<StepUsageDeclaration> steps;
    private final Map<String,String> flowLevelAlias;
    private final Map<String, String> customMapping;

    public FlowDefinitionImpl(String name, String description) {
        this.name = name;
        this.description = description;
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        customMapping = new HashMap<>();
    }

    public FlowDefinitionImpl(STFlow stFlow)
    {
        this.name = stFlow.getName();
        this.description = stFlow.getSTFlowDescription();
        String[] output = stFlow.getSTFlowOutput().split(",");
        flowOutputs = Arrays.asList(output);
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        customMapping = new HashMap<>();
        STStepsInFlow stStepsInFlow = stFlow.getSTStepsInFlow();
        for(int i = 0; i < stStepsInFlow.getSTStepInFlow().size(); i++)
        {
            steps.add(new StepUsageDeclarationImpl(stStepsInFlow.getSTStepInFlow().get(i)));
        }
        STFlowLevelAliasing stFlowLevelAliasing = stFlow.getSTFlowLevelAliasing();
        if(stFlowLevelAliasing!=null) {
            List<STFlowLevelAlias> stFlowLevelAliasList = stFlowLevelAliasing.getSTFlowLevelAlias();
            for (STFlowLevelAlias stFlowLevelAlias : stFlowLevelAliasList) {
                flowLevelAlias.put(stFlowLevelAlias.getSourceDataName(), stFlowLevelAlias.getAlias());
            }
        }
        STCustomMappings stCustomMappings = stFlow.getSTCustomMappings();
        if(stCustomMappings!=null) {
            List<STCustomMapping> stCustomMappingList = stCustomMappings.getSTCustomMapping();
            for (STCustomMapping stCustomMapping : stCustomMappingList) {
                customMapping.put(stCustomMapping.getSourceStep()+"."+ stCustomMapping.getSourceData(),
                        stCustomMapping.getTargetStep()+"."+ stCustomMapping.getTargetData());
            }
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
    @Override
    public String getFlowLevelAlias(String sourceDataName)
    {
        return flowLevelAlias.get(sourceDataName);
    }

    @Override
    public String getFlowLevelCustomMapping(String name)
    {
        return customMapping.get(name);
    }
}
