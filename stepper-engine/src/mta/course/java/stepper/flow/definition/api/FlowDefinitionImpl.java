package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.*;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.*;

public class FlowDefinitionImpl implements FlowDefinition {

    private final String name;
    private final String description;
    private final List<String> flowOutputs;
    private final List<String> flowFreeInputs;//TODO create a function that will return the free inputs
    private final List<StepUsageDeclaration> steps;
    private final Map<String,String> flowLevelAlias;
    private final Map<String, String> customMapping;
    private  boolean readonly;

    public FlowDefinitionImpl(String name, String description) {//TODO delete this after we are sure we don't need it
        this.name = name;
        this.description = description;
        flowOutputs = new ArrayList<>();
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        customMapping = new HashMap<>();
        flowFreeInputs = new ArrayList<>();
        readonly = true;
    }

    public FlowDefinitionImpl(STFlow stFlow)
    {
        readonly = true;
        this.name = stFlow.getName();
        flowFreeInputs = new ArrayList<>();
        this.description = stFlow.getSTFlowDescription();
        String[] output = stFlow.getSTFlowOutput().split(",");
        flowOutputs = Arrays.asList(output);
        steps = new ArrayList<>();
        flowLevelAlias = new HashMap<>();
        customMapping = new HashMap<>();
        STStepsInFlow stStepsInFlow = stFlow.getSTStepsInFlow();
        for(int i = 0; i < stStepsInFlow.getSTStepInFlow().size(); i++)
        {
            StepUsageDeclaration stepUsageDeclaration = new StepUsageDeclarationImpl(stStepsInFlow.getSTStepInFlow().get(i));
            steps.add(stepUsageDeclaration);//TODO make sure changes didnt fuck anything up
            if(!stepUsageDeclaration.getStepDefinition().isReadonly())//if we found a step that is not readonly, the flow is not readonly
                readonly = false;
        }
        STFlowLevelAliasing stFlowLevelAliasing = stFlow.getSTFlowLevelAliasing();
        if(stFlowLevelAliasing!=null) {
            List<STFlowLevelAlias> stFlowLevelAliasList = stFlowLevelAliasing.getSTFlowLevelAlias();
            for (STFlowLevelAlias stFlowLevelAlias : stFlowLevelAliasList) {
                flowLevelAlias.put(stFlowLevelAlias.getStep()+"."+stFlowLevelAlias.getSourceDataName(),
                        stFlowLevelAlias.getStep()+"."+stFlowLevelAlias.getAlias());
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
        for(StepUsageDeclaration step :steps)
        {
            for(DataDefinitionDeclaration dataDefinitionDeclaration : step.getStepDefinition().inputs())
            {
                if(!flowOutputs.contains(dataDefinitionDeclaration.getName()))
                    flowFreeInputs.add(dataDefinitionDeclaration.getName());
            }
        }
        return null;//TODO: implement

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
        String nameAfterAlias = flowLevelAlias.get(name);
        if (nameAfterAlias !=null)
            return customMapping.get(nameAfterAlias);
        else
            return customMapping.get(name);
    }
}
