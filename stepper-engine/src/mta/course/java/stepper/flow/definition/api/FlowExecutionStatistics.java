package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.sql.Time;
import java.util.List;

public class FlowExecutionStatistics
{
    private final Time startTime;
    private final String flowName;
    private final String flowId;
    private final FlowResult flowResult;
    private final double duration;
    private FlowDefinition flow;
    private StepExecutionContext context;

    public FlowExecutionStatistics(Time startTime, String flowName, String flowId, FlowResult flowResult, double duration,FlowDefinition flow, StepExecutionContext context) {
        this.startTime = startTime;//TODO check if format matches HH:MM:SS
        this.flowName = flowName;
        this.flowId = flowId;
        this.flowResult = flowResult;
        this.duration = duration;
        this.flow = flow;
        this.context = context;
        //TODO add details about free inputs and outputs
    }
    public void printStatistics(){
        System.out.println("Flow start time: " + startTime);
        System.out.println("Flow name: " + flowName);
        System.out.println("Flow id: " + flowId);
        System.out.println("Flow result: " + flowResult);
        System.out.println("Flow duration: " + duration);
        System.out.println("----INPUTS----");
        printFreeInputsExectued();
        System.out.println("----OUTPUTS----");
        printFreeOutputsExectued();
    }

    public void printFreeInputsExectued() {
        List<DataDefinitionDeclaration> inputs = flow.getFlowFreeInputs();
        List<String> inputString = flow.getFlowFreeInputsString();
        for (int i=0; i<inputs.size(); i++){
            String[] freeInput = inputString.get(i).split("\\.");
            System.out.println(freeInput[1]);
            System.out.println(inputs.get(i).dataDefinition().getName());
            Object content = context.dataValueReturn(inputString.get(i));
            if (content.equals(""))
                System.out.println("Null");
            else
                System.out.println(content);
            System.out.println(inputs.get(i).necessity());
            System.out.println();
        }
    }

    public void printFreeOutputsExectued() {
        List<DataDefinitionDeclaration> outputs = flow.getFlowFreeOutputs();
        List<String> outputString = flow.getFlowFreeOutputsString();
        for (int i=0; i<outputs.size(); i++){
            String[] freeOutput = outputString.get(i).split("\\.");
            System.out.println(freeOutput[1]);
            System.out.println(outputs.get(i).dataDefinition().getName());
            Object content = context.dataValueReturn(outputString.get(i));
            if (content.equals(""))
                System.out.println("Not created due to failure in flow");
            else
                System.out.println(content);
            System.out.println();
        }
    }
}
