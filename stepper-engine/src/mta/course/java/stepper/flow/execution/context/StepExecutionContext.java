package mta.course.java.stepper.flow.execution.context;

import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;

import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

public interface StepExecutionContext {
    <T> T getDataValue(String dataName, Class<T> expectedDataType);
    boolean storeDataValue(String dataName, Object value);

    void addStep(String key, Object value,DataDefinition dataDefinition, String alias, String customMapping);

    public void addSummaryLine(String key, String summaryLine);
    public void addStepAlias(String key, String stepAlias, boolean skipIfFail);
    public void addLogLine(String key, String log);
    public ArrayList<String> getLogs(String key) ;
    public Map<String, ArrayList<String>> getLogs();
    public String getSummaryLine(String key);
    public String getAlias(String key,Class<?> theExpectedDataType);
    public Map<String, Object> getDataValuesMap();
    public String getCustomMapping(String key);
    public String getStepAlias(String key);
    Object dataValueReturn (String key);

    void addFlowLevelAlias(String key, String alias, Class<?> theExpectedDataType);
    public DataDefinitionRegistry getDataDefinitionRegistry(Class<?> theExpectedDataType);

    Map<String, DataDefinition> getDataDefinitions();

    Map<String, String> getSummaryLines();

    Map<String, String> getFlowLevelAliasing();

    Map<String, String> getCostumeMapping();

    Map<AutoMapping,Object> getAutoMapping();

    Map<String, Queue<stepAliasing>> getStepLevelAliasing();

    FlowDefinition getFlowDefinition();


    /*
     some more utility methods:
     allow step to store log lines
     allow steps to declare their summary line
    */
}
