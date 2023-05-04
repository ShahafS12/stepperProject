package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.List;
import java.util.Map;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();
    void createFreeInputOutputLists();
    boolean isReadonly();
    List<DataDefinitionDeclaration> getFlowFreeInputs();
    void addFlowOutput(String outputName);
    void addFlowStep(StepUsageDeclaration stepUsageDeclaration);
    String getFlowLevelAlias(String sourceDataName);
    boolean isDataDefEquals(String stepName, String outputName,String dataDefName);
    boolean isReadOnly();
    void convertInputDataFromArrayStringToDD (List<String> inputs);
    void convertOutpusDataFromArrayStringToDD (List<String> outputs);
    List<String> getFlowFreeInputsString();
    List<DataDefinitionDeclaration> getFlowFreeOutputs();
    List<String> getFlowFreeOutputsString();
    public Map<String,String> getCustomMapping();
    void printFreeInputs();
    void printFreeOutputs();
    Map<String,Class<?>> getAllInputs();
    Map<String,Class<?>> getAllOutputs();
    List<String> getFinalStepNames();
    Map<String,String> getAllFlowLevelAlias();


    String getFlowLevelCustomMapping(String name);
}
