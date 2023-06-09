package mta.course.java.stepper.main;

import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.StepDefinitionRegistry;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    public static void main(String[] args) {



        FlowDefinition flow1 = new FlowDefinitionImpl("Flow 1", "Hello world");
        flow1.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.HELLO_WORLD.getStepDefinition()));
        flow1.createFreeInputOutputLists();

        FlowDefinition flow2 = new FlowDefinitionImpl("Flow 2", "show two person details");
        flow2.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.HELLO_WORLD.getStepDefinition()));
        flow2.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.ZIPPER.getStepDefinition(), "Person 1 Details"));
        //flow2.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.PERSON_DETAILS.getStepDefinition(), "Person 2 Details"));
        flow2.getFlowFormalOutputs().add("DETAILS");
        flow2.createFreeInputOutputLists();

        FlowDefinition flow3 = new FlowDefinitionImpl("Flow 3", "Checking steps");
        flow3.getFlowSteps().add(new StepUsageDeclarationImpl(StepDefinitionRegistry.COLLECT_FILES_IN_FOLDER.getStepDefinition()));


        FLowExecutor fLowExecutor = new FLowExecutor();

        FlowExecution flow3Execution1 = new FlowExecution("2", flow2);
        fLowExecutor.executeFlow(flow3Execution1);

/*
        FlowExecution flow2Execution1 = new FlowExecution("1", flow2);
        // collect all user inputs and store them on the flow execution object

        fLowExecutor.executeFlow(flow2Execution1);

        FlowExecution flow2Execution2 = new FlowExecution("2", flow2);
        // collect all user inputs and store them on the flow execution object
        fLowExecutor.executeFlow(flow2Execution1);
        */
    }
}
