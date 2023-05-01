package mta.course.java.stepper.flow.execution;

import mta.course.java.stepper.flow.definition.api.FlowDefinition;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

public class FlowExecution {

    private final String uniqueId;
    private final FlowDefinition flowDefinition;
    private Instant start, end;
    private long totalTime;
    private FlowExecutionResult flowExecutionResult;
    private Map<String,Object> freeInputs;
    private int countHowManyTimesExecution;

    // lots more data that needed to be stored while flow is being executed...

    public FlowExecution(String uniqueId, FlowDefinition flowDefinition) {
        this.uniqueId = uniqueId;
        this.flowDefinition = flowDefinition;
        start = Instant.now();
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

    public long timeTakenForFlow (){
        endTime();
        return totalTime;}
}
