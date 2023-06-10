package mta.course.java.stepper.stepper;

import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.step.api.StepExecutionStatistics;

import java.util.HashMap;
import java.util.Map;

public class FlowExecutionsStatistics
{
    private final String flowName;
    private int countHowManyTimesExecution;
    private double averageDuration;
    private Map<String,StepExecutionStatistics> stepExecutionStatisticsMap;
    //TODO add details about steps
    public FlowExecutionsStatistics(String flowName) {
        countHowManyTimesExecution = 0;
        averageDuration = 0;
        stepExecutionStatisticsMap = new HashMap<>();
        this.flowName = flowName;
    }
    public synchronized void addFlowExecutionStatistics(FlowExecutionStatistics flowExecutionStatistics){
        averageDuration = (averageDuration * countHowManyTimesExecution + flowExecutionStatistics.getDuration()) / (countHowManyTimesExecution + 1);
        countHowManyTimesExecution++;
        for(String step:flowExecutionStatistics.getStepExecutionStatisticsMap().keySet()){
            if(stepExecutionStatisticsMap.containsKey(step)){
                stepExecutionStatisticsMap.get(step).addStepExecutionStatistics(flowExecutionStatistics.getStepExecutionStatisticsMap().get(step));
            }
            else{
                stepExecutionStatisticsMap.put(step,flowExecutionStatistics.getStepExecutionStatisticsMap().get(step));
            }
        }

    }
    public void printStatistics(){
        System.out.println("Flow name: " + flowName);
        System.out.println("Number of executions: " + countHowManyTimesExecution);
        System.out.println("Average duration: " + averageDuration);
        System.out.println("----STEPS----");
        for (Map.Entry<String, StepExecutionStatistics> entry : stepExecutionStatisticsMap.entrySet()) {
            System.out.println("Step name: " + entry.getKey());
            entry.getValue().printStatistics();
        }
    }
    public String getFlowName() {
        return flowName;
    }
    public int getCountHowManyTimesExecution() {
        return countHowManyTimesExecution;
    }
    public double getAverageDuration() {
        return averageDuration;
    }
}