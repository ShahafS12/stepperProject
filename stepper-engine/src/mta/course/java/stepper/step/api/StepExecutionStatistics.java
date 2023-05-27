package mta.course.java.stepper.step.api;

public class StepExecutionStatistics
{
    private final String stepName;
    private int countHowManyTimesExecution;
    private double averageDuration;
    //TODO add details about steps
    public StepExecutionStatistics() {
        countHowManyTimesExecution = 0;
        averageDuration = 0;
        stepName = "Null";
    }
    public StepExecutionStatistics(double duration) {
        countHowManyTimesExecution = 1;
        averageDuration = duration;
        stepName = "Null";
    }
    public StepExecutionStatistics(String name, double duration) {
        stepName = name;
        countHowManyTimesExecution = 1;
        averageDuration = duration;
    }
    public String getStepName() {
        return stepName;
    }
    public void addStepExecutionStatistics(StepExecutionStatistics stepExecutionStatistics){
        averageDuration = (averageDuration * countHowManyTimesExecution + stepExecutionStatistics.getAverageDuration()) / (countHowManyTimesExecution + 1);
        countHowManyTimesExecution++;
    }
    public void printStatistics(){
        System.out.println("Number of executions: " + countHowManyTimesExecution);
        System.out.println("Average duration: " + averageDuration+"ms");
        System.out.println();

    }
    public int getCountHowManyTimesExecution() {
        return countHowManyTimesExecution;
    }
    public double getAverageDuration() {
        return averageDuration;
    }

}
