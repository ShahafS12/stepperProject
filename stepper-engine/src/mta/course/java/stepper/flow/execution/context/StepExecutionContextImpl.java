package mta.course.java.stepper.flow.execution.context;

import mta.course.java.stepper.dd.api.DataDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private final Map<String,DataDefinition> dataDefinitions;
    private final Map<String, ArrayList<String>> logs;
    private final Map<String, String> summaryLines;
    private final Map<String, String> FlowLevelAliases;

    public StepExecutionContextImpl() {
        dataValues = new HashMap<>();
        dataDefinitions = new HashMap<>();
        logs = new HashMap<>();
        summaryLines = new HashMap<>();
        FlowLevelAliases = new HashMap<>();
    }

    @Override
    public void addLogLine(String key, String log) {
        if (logs.get(key) == null) {
            logs.put(key, new ArrayList<String>());
        }
        ArrayList<String> list = logs.get(key);
        list.add(log);
    }

    @Override
    public void addSummaryLine(String key, String summaryLine) { summaryLines.put(key, summaryLine);    }

    @Override
    public ArrayList<String> getLogs(String key) {
        return logs.get(key);
    }

    @Override
    public String getSummaryLine(String key) {
        return summaryLines.get(key);
    }

    @Override
    public <T> T getDataValue(String dataName, Class<T> expectedDataType) {
        // assuming that from the data name we can get to its data definition
        DataDefinition theExpectedDataDefinition = (DataDefinition) dataDefinitions.get(dataName);

        if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = dataValues.get(dataName);
            // what happens if it does not exist ?

            return expectedDataType.cast(aValue);
        } else {
            // error handling of some sort...
        }

        return null;
    }

    @Override
    public boolean storeDataValue(String dataName, Object value) {
        // assuming that from the data name we can get to its data definition
        DataDefinition theData = (DataDefinition) dataDefinitions.get(dataName);

        // we have the DD type, so we can make sure that its from the same type
        if (theData.getType().isAssignableFrom(value.getClass())) {
            dataValues.put(dataName, value);
            return true;
        } else {
            // error handling of some sort...
        }

        return false;
    }

    @Override
    public void addStep(String key, Object value,DataDefinition dataDefinition, String alias)
    {
        dataValues.put(key, value);
        dataDefinitions.put(key,dataDefinition);
        FlowLevelAliases.put(key,alias);
    }
    @Override
    public String getAlias(String key)
    {
        return FlowLevelAliases.get(key);
    }
}
