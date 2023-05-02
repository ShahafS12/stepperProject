package mta.course.java.stepper.flow.definition.api;

import java.sql.Time;

public class FlowExecutionStatistics
{
    private final Time startTime;
    private final String flowName;
    private final String flowId;
    private final FlowResult flowResult;
    private final double duration;
    public FlowExecutionStatistics(Time startTime, String flowName, String flowId, FlowResult flowResult, double duration) {
        this.startTime = startTime;//TODO check if format matches HH:MM:SS
        this.flowName = flowName;
        this.flowId = flowId;
        this.flowResult = flowResult;
        this.duration = duration;
        //TODO add details about free inputs and outputs
    }
    public void printStatistics(){
        System.out.println("Flow start time: " + startTime);
        System.out.println("Flow name: " + flowName);
        System.out.println("Flow id: " + flowId);
        System.out.println("Flow result: " + flowResult);
        System.out.println("Flow duration: " + duration);
    }
}
