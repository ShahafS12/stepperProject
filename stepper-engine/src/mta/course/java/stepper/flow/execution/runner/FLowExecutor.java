package mta.course.java.stepper.flow.execution.runner;

import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.execution.context.StepExecutionContextImpl;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.api.StepResult;

import java.util.List;

public class FLowExecutor {

    public void executeFlow(FlowExecution flowExecution) {

        System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]");

        StepExecutionContext context = new StepExecutionContextImpl(); // actual object goes here...
        List<StepUsageDeclaration> steps = flowExecution.getFlowDefinition().getFlowSteps();
        for (StepUsageDeclaration step : steps) {
            StepDefinition stepDefinition = step.getStepDefinition();
            List<DataDefinitionDeclaration> inputs = stepDefinition.inputs();
            List<DataDefinitionDeclaration> outputs = stepDefinition.outputs();
            for (DataDefinitionDeclaration input : inputs) {
                context.addStep(input.getName(), input.dataDefinition().getValue(), input.dataDefinition());
            }
            for (DataDefinitionDeclaration output : outputs) {
                context.addStep(output.getName(), output.dataDefinition().getType(), output.dataDefinition());
            }
        }
        // populate context with all free inputs (mandatory & optional) that were given from the user
        // (typically stored on top of the flow execution object)

        // start actual execution
        for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
            StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
            System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
            StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
            System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
            // check if should continue etc..
        }


        System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
    }
}