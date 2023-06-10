package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.flow.InputWithStepName;
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

    List<InputWithStepName> getMandatoryInputs();

    Map<String, String> getInnitialDataValues();

    List<InputWithStepName> getOptionalInputs();

    void addFlowOutput(String outputName);

    Map<String, String> getAllFlowInputsWithNeccecity();

    void addFlowStep(StepUsageDeclaration stepUsageDeclaration);
    String getFlowLevelAlias(String sourceDataName);
    boolean isDataDefEquals(String stepName, String outputName,String dataDefName);
    boolean isReadOnly();
    List<InputWithStepName> getOutputs();
    void convertInputDataFromArrayStringToDD (List<String> inputs);
    void convertOutpusDataFromArrayStringToDD (List<String> outputs);
    List<String> getFlowFreeInputsString();
    List<DataDefinitionDeclaration> getFlowFreeOutputs();
    List<String> getFlowFreeOutputsString();
    public Map<String,String> getCustomMapping();
    void printFreeInputs();
    void printFreeOutputs();
    String getUniqueId();
    Map<String,Class<?>> getAllInputs();
    Map<String,Class<?>> getAllOutputs();
    List<String> getFinalStepNames();
    Map<String,String> getAllFlowLevelAlias();
    List<String> getPreAliasFlowFreeInputs();
    String getStepName(int counter);


        String getFlowLevelCustomMapping(String name);

    boolean isNumeric(String str);

    List<Continuation> getContinuations();
}
