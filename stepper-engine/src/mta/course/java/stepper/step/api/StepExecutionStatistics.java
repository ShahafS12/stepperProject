package mta.course.java.stepper.step.api;

public class StepExecutionStatistics
{
    private final String stepName;
    private int countHowManyTimesExecution;
    private double averageDuration;
    //TODO add details about steps
    public StepExecutionStatistics(String stepName) {
        countHowManyTimesExecution = 0;
        averageDuration = 0;
        this.stepName = stepName;
    }
    public StepExecutionStatistics(String stepName,double duration) {
        countHowManyTimesExecution = 1;
        averageDuration = duration;
        this.stepName = stepName;
    }
    public void addStepExecutionStatistics(StepExecutionStatistics stepExecutionStatistics){
        averageDuration = (averageDuration * countHowManyTimesExecution + stepExecutionStatistics.getAverageDuration()) / (countHowManyTimesExecution + 1);
        countHowManyTimesExecution++;
    }
    public void printStatistics(){
        System.out.println("Step name: " + stepName);
        System.out.println("Number of executions: " + countHowManyTimesExecution);
        System.out.println("Average duration: " + averageDuration);
    }
    public String getStepName() {
        return stepName;
    }
    public int getCountHowManyTimesExecution() {
        return countHowManyTimesExecution;
    }
    public double getAverageDuration() {
        return averageDuration;
    }

}
