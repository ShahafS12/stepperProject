package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import javax.xml.crypto.Data;
import java.util.List;

public interface FlowDefinition {
    String getName();
    String getDescription();
    List<StepUsageDeclaration> getFlowSteps();
    List<String> getFlowFormalOutputs();

    void validateFlowStructure();

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

    void printFreeInputs();
    void printFreeOutputs();


    String getFlowLevelCustomMapping(String name);
}
