package mta.course.java.stepper.flow.execution.context;

import mta.course.java.stepper.dd.api.DataDefinition;

import java.util.ArrayList;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);

    void addStep(String key, Object value,DataDefinition dataDefinition, String alias, String customMapping);

    public void addSummaryLine(String key, String summaryLine);
    public void addStepAlias(String key, String stepAlias, boolean skipIfFail);
    public void addLogLine(String key, String log);
    public ArrayList<String> getLogs(String key) ;
    public String getSummaryLine(String key);
    public String getAlias(String key,Class<?> theExpectedDataType);
    public String getCustomMapping(String key);
    public String getStepAlias(String key);
    Object dataValueReturn (String key);


    /*
     some more utility methods:
     allow step to store log lines
     allow steps to declare their summary line
    */
}
