package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.flow.execution.FlowExecutionResult;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepExecutionStatistics;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlowExecutionStatistics
{
    private final Time startTime;
    private final String dateTime;
    private final String flowName;
    private final String flowId;
    private final FlowExecutionResult flowResult;
    private final double duration;
    private FlowDefinition flow;
    private Map<String, SingleStepExecutionData> singleStepExecutionDataMap;
    private Map<String,StepExecutionStatistics> stepExecutionStatisticsMap;
    private StepExecutionContext context;
    private List<SingleStepExecutionData> singleStepExecutionDataList;
    private String executedUser;


    public FlowExecutionStatistics(Time startTime, String flowName, String flowId, FlowExecutionResult FlowExecutionResult, double duration,FlowDefinition flow,
                                   StepExecutionContext context,Map<String, SingleStepExecutionData> singleStepExecutionDataMap,
                                   Map<String,StepExecutionStatistics> stepExecutionStatisticsMap, String executedUser){
        this.startTime = startTime;
        this.flowName = flowName;
        this.flowId = flowId;
        this.flowResult = FlowExecutionResult;
        this.duration = duration;
        this.flow = flow;
        this.context = context;
        this.singleStepExecutionDataMap = singleStepExecutionDataMap;
        this.stepExecutionStatisticsMap = stepExecutionStatisticsMap;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        this.dateTime = dateFormat.format(this.startTime);
        this.executedUser = executedUser;
        //TODO add details about free inputs and outputs
    }
    public FlowExecutionStatistics(Time startTime, String flowName, String flowId, FlowExecutionResult FlowExecutionResult, double duration,FlowDefinition flow,
                                   StepExecutionContext context,Map<String, SingleStepExecutionData> singleStepExecutionDataMap,
                                   Map<String,StepExecutionStatistics> stepExecutionStatisticsMap, List<SingleStepExecutionData> singleStepExecutionDataList, String executedUser){
        this.startTime = startTime;
        this.flowName = flowName;
        this.flowId = flowId;
        this.flowResult = FlowExecutionResult;
        this.duration = duration;
        this.flow = flow;
        this.context = context;
        this.singleStepExecutionDataMap = singleStepExecutionDataMap;
        this.stepExecutionStatisticsMap = stepExecutionStatisticsMap;
        this.singleStepExecutionDataList = singleStepExecutionDataList;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        this.dateTime = dateFormat.format(this.startTime);
        this.executedUser = executedUser;
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
        System.out.println("----STEPS----");
        for (Map.Entry<String, SingleStepExecutionData> entry : singleStepExecutionDataMap.entrySet()) {
            System.out.println("Step name: " + entry.getKey());
            entry.getValue().printStatistics();
        }
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
    public String getFlowName() {
        return flowName;
    }
    public String getFlowId() {
        return flowId;
    }
    public FlowExecutionResult getFlowResult() {
        return flowResult;
    }
    public List<SingleStepExecutionData> getSingleStepExecutionDataList() {
        return singleStepExecutionDataList;
    }
    public double getDuration() {
        return duration;
    }
    public FlowDefinition getFlow() {
        return flow;
    }
    public Time getStartTime() {
        return startTime;
    }
    public String getDateTime() {
        return dateTime;
    }
    public Map<String, SingleStepExecutionData> getSingleStepExecutionDataMap() {
        return singleStepExecutionDataMap;
    }
    public Map<String,StepExecutionStatistics> getStepExecutionStatisticsMap() {
        return stepExecutionStatisticsMap;
    }
    public Map<String, Object> getUserInputsMap(){
        Map<String, Object> userInputsMap = new HashMap<>();
        List<String> inputString =flow.getFlowFreeInputsString();
        for (String i : inputString){
            //get the value after the last dot
            String[] freeInput = i.split("\\.");
            //put the value in the map
            userInputsMap.put(i,context.getDataValue(i, Object.class));
        }
        List<String> outputString =flow.getFlowFreeOutputsString();
        for (String i : outputString){
            //get the value after the last dot
            String[] freeOutput = i.split("\\.");
            //check if actual value is null, if so put "" in the map
            if (context.getDataValue(i, Object.class).toString().equals("class java.lang.Number")
            || context.getDataValue(i, Object.class).toString().equals("class java.lang.String")){
                //userInputsMap.put(freeOutput[1], "");
                //System.out.println("Null value in output");
            }
            else
            //put the value in the map
                userInputsMap.put(i,context.getDataValue(i, Object.class));
        }
        return userInputsMap;
    }

    public String getExecutedUserName() {
        return executedUser;
    }
}
