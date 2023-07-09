package mta.course.java.stepper.stepper;

import mta.course.java.stepper.flow.definition.api.FlowDefinition;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface StepperDefinition
{
    ArrayList<String> getFlowNames();
    ArrayList<FlowDefinition> getFlows();
    List<String> getFlowFormalInputs(String flowName);
    List<String> getFlowFormalOutputs(String flowName);
    void addFlow(FlowDefinition flowName);
    FlowDefinition getFlowDefinition(String flowName);

    int getMaxThreads();
}
