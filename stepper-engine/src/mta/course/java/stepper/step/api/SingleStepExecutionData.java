package mta.course.java.stepper.step.api;

import java.util.List;

public class SingleStepExecutionData
{
    private final double duration;
    private final StepResult success;
    private String summaryLine;
    private List<String> logs;
    private String stepName;
    public SingleStepExecutionData(double duration, StepResult success, String summaryLine, List<String> logs,String stepName)
    {
        this.duration = duration;
        this.success = success;
        this.summaryLine = summaryLine;
        this.logs = logs;
        this.stepName = stepName;
    }
    public double getDuration()
    {
        return duration;
    }
    public String getStepName()
    {
        return stepName;
    }
    public StepResult getSuccess()
    {
        return success;
    }
    public String getSummaryLine()
    {
        return summaryLine;
    }
    public List<String> getLogs()
    {
        return logs;
    }
    public void printStatistics(){
        System.out.println("Duration: " + duration+"ms");
        System.out.println("Success: " + success);
        System.out.println("Summary line: " + summaryLine);
        System.out.println("Logs: ");
        for (String log : logs) {
            String[] logParts = log.split("\\|");
            for(String logPart : logParts)
                System.out.println(logPart+"\n");
        }
        System.out.println();
    }
}
