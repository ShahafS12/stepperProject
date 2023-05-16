package mta.course.java.stepper.flow.execution;

import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepExecutionStatistics;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class FlowExecution {

    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    private Instant start, end;
    private long totalTime;
    private FlowExecutionResult flowExecutionResult;
    private Map<String,Object> freeInputs;
    private int countHowManyTimesExecution;
    private Map<String, SingleStepExecutionData> singleStepExecutionDataMap;

    private Map<String,StepExecutionStatistics> stepExecutionStatisticsMap;

    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = flowDefinition.getUniqueId();
        this.flowDefinition = flowDefinition;
        start = Instant.now();
        singleStepExecutionDataMap = new HashMap<>();
        stepExecutionStatisticsMap = new HashMap<>();
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    public FlowExecutionResult getFlowExecutionResult() {
        return flowExecutionResult;
    }

    public void addingExectionCounter(){countHowManyTimesExecution++;}

    public int getCounter(){return countHowManyTimesExecution;}

    public void endTime(){
        end = Instant.now();
        totalTime =  Duration.between(start, end).toMillis() ;
    }
    public void setFlowExecutionResult(FlowExecutionResult flowExecutionResult) {
        this.flowExecutionResult = flowExecutionResult;
    }

    public long timeTakenForFlow (){
        endTime();
        return totalTime;
    }
    public Map<String, SingleStepExecutionData> getSingleStepExecutionDataMap() {
        return singleStepExecutionDataMap;
    }
    public Map<String,StepExecutionStatistics> getStepExecutionStatisticsMap() {
        return stepExecutionStatisticsMap;
    }
}
