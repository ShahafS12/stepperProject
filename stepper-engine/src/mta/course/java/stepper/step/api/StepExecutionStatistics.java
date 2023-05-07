package mta.course.java.stepper.step.api;

public class StepExecutionStatistics
{
    private int countHowManyTimesExecution;
    private double averageDuration;
    //TODO add details about steps
    public StepExecutionStatistics() {
        countHowManyTimesExecution = 0;
        averageDuration = 0;
    }
    public StepExecutionStatistics(double duration) {
        countHowManyTimesExecution = 1;
        averageDuration = duration;
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
