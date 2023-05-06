package mta.course.java.stepper.stepper;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;

public class FlowExecutionsStatistics
{
    private final String flowName;
    private int countHowManyTimesExecution;
    private double averageDuration;
    //TODO add details about steps
    public FlowExecutionsStatistics(String flowName) {
        countHowManyTimesExecution = 0;
        averageDuration = 0;
        this.flowName = flowName;
    }
    public void addFlowExecutionStatistics(FlowExecutionStatistics flowExecutionStatistics){
        averageDuration = (averageDuration * countHowManyTimesExecution + flowExecutionStatistics.getDuration()) / (countHowManyTimesExecution + 1);
        countHowManyTimesExecution++;
    }
    public void printStatistics(){
        System.out.println("Flow name: " + flowName);
        System.out.println("Number of executions: " + countHowManyTimesExecution);
        System.out.println("Average duration: " + averageDuration);
    }
}