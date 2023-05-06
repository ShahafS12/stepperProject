package mta.course.java.stepper.flow.execution.runner;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.definition.api.FlowResult;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.execution.context.StepExecutionContextImpl;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.api.StepResult;

import java.sql.SQLOutput;
import java.sql.Time;
import java.time.Duration;
import java.util.List;

public class FLowExecutor {

    public FlowExecutionStatistics executeFlow(FlowExecution flowExecution) {

        System.out.println("Starting execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]");
        flowExecution.addingExectionCounter();
        Time executionStartTime = new Time(System.currentTimeMillis());
        FlowResult flowResult = FlowResult.SUCCESS;

        StepExecutionContext context = new StepExecutionContextImpl(); // actual object goes here...
        List<StepUsageDeclaration> steps = flowExecution.getFlowDefinition().getFlowSteps();
        List<String> flowFreeInputsString = flowExecution.getFlowDefinition().getPreAliasFlowFreeInputs();
        List<String> flowFreeOutputsString = flowExecution.getFlowDefinition().getFlowFreeOutputsString();
        for (StepUsageDeclaration step : steps) {
            StepDefinition stepDefinition = step.getStepDefinition();
            List<DataDefinitionDeclaration> inputs = stepDefinition.inputs();
            List<DataDefinitionDeclaration> outputs = stepDefinition.outputs();
            context.addStepAlias(step.getStepName(), step.getFinalStepName(),step.skipIfFail());
            for (DataDefinitionDeclaration input : inputs) {
                if(flowFreeInputsString.contains(step.getFinalStepName() + "." +input.getName())) {
                    context.addStep(step.getFinalStepName() + "." + input.getName(),
                            input.dataDefinition().getValue(input.userString()),
                            input.dataDefinition(),
                            flowExecution.getFlowDefinition().getFlowLevelAlias(step.getFinalStepName() + "." + input.getName()),
                            flowExecution.getFlowDefinition().getFlowLevelCustomMapping(step.getFinalStepName() + "." + input.getName())
                    );
                }
            }
            for (DataDefinitionDeclaration output : outputs) {
                context.addStep(step.getFinalStepName() + "." + output.getName(),
                        output.dataDefinition().getType(),
                        output.dataDefinition(),
                        flowExecution.getFlowDefinition().getFlowLevelAlias(step.getFinalStepName() + "." + output.getName()),
                        flowExecution.getFlowDefinition().getFlowLevelCustomMapping(step.getFinalStepName() + "." + output.getName())
                );
            }
        }
        // populate context with all free inputs (mandatory & optional) that were given from the user
        // (typically stored on top of the flow execution object)

        // start actual execution
        for (int i = 0; i < flowExecution.getFlowDefinition().getFlowSteps().size(); i++) {
            StepUsageDeclaration stepUsageDeclaration = flowExecution.getFlowDefinition().getFlowSteps().get(i);
            System.out.println("Starting to execute step: " + stepUsageDeclaration.getFinalStepName());
            StepResult stepResult = stepUsageDeclaration.getStepDefinition().invoke(context);
            if (stepResult == StepResult.FAILURE)
                flowResult = FlowResult.FAILURE;
            if(stepResult == StepResult.WARNING && flowResult != FlowResult.FAILURE)
                flowResult = FlowResult.WARNING;
            System.out.println("Done executing step: " + stepUsageDeclaration.getFinalStepName() + ". Result: " + stepResult);
            // check if should continue etc..
        }



        double totalTimeFlow = flowExecution.timeTakenForFlow();
        System.out.println("End execution of flow " + flowExecution.getFlowDefinition().getName() + " [ID: " + flowExecution.getUniqueId() + "]. Status: " + flowExecution.getFlowExecutionResult());
        System.out.println("Total Time: " + totalTimeFlow + " ms");
        FlowExecutionStatistics result = new FlowExecutionStatistics(executionStartTime,flowExecution.getFlowDefinition().getName(),flowExecution.getUniqueId(),flowResult,totalTimeFlow, flowExecution.getFlowDefinition(), context);
        return result;
    }
}
