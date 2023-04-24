package mta.course.java.stepper.flow.execution.context;

import mta.course.java.stepper.dd.api.DataDefinition;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);

    void addStep(String key, Object value, DataDefinition dataDefinition);

    /*
     some more utility methods:
     allow step to store log lines
     allow steps to declare their summary line
    */
}
