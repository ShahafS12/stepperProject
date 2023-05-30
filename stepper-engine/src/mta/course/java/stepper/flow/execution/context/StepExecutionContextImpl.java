package mta.course.java.stepper.flow.execution.context;

import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.sql.Time;
import java.time.Instant;
import java.util.*;

public class StepExecutionContextImpl implements StepExecutionContext {

    private final Map<String, Object> dataValues;
    private final Map<String,DataDefinition> dataDefinitions;
    private final Map<String, ArrayList<String>> logs;
    private final Map<String, String> summaryLines;
    private final Map<String, String> FlowLevelAliases;
    private final Map<String, String> CustomMapping;
    private final Map<AutoMapping,Object> AutoMappingMap;
    private final Map<String, Queue<stepAliasing>> stepAliases;
    private final Map<String,String> flowFreeInputs;
    private final FlowDefinition flowDef;


    public StepExecutionContextImpl(FlowDefinition flowDefinition) {
        dataValues = new HashMap<>();
        dataDefinitions = new HashMap<>();
        logs = new HashMap<>();
        summaryLines = new HashMap<>();
        FlowLevelAliases = new HashMap<>();
        CustomMapping = new HashMap<>();
        AutoMappingMap = new HashMap<>();
        stepAliases = new HashMap<>();
        flowFreeInputs = new HashMap<>();
        flowDef = flowDefinition;
    }

    @Override
    public void addLogLine(String key, String log) {
        if (logs.get(key) == null) {
            logs.put(key, new ArrayList<String>());
        }
        ArrayList<String> list = logs.get(key);
        Time time = new Time(System.currentTimeMillis());
        log = log + "|" + time.toString();
        list.add(log);
    }
    @Override
    public Map<String, Object> getDataValuesMap() {
        return dataValues;
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
        if(CustomMapping.containsKey(dataName)){
            dataName = CustomMapping.get(dataName);
        }
        else{
            for(AutoMapping autoMapping : AutoMappingMap.keySet())
            {
                String tmp[] = dataName.split("\\.");
                if(autoMapping.getName().equals(tmp[1])&&autoMapping.getType().equals(expectedDataType))
                    return expectedDataType.cast(AutoMappingMap.get(autoMapping));
            }
        }

        // In case of enum, we need to get the value from the enum class
        if (theExpectedDataDefinition.getType().equals(Enum.class)){
            Object aValue = dataValues.get(dataName);
            if (aValue == null) {//if value is null, try to get it from auto mapping
                String[] split = dataName.split("\\.");//to get actual output name for auto mapping
                aValue = AutoMappingMap.get(new AutoMapping(expectedDataType,split[1]));
                if(aValue == null) {
                    System.out.println("Didnt find value for " + dataName + " in AutoMapping");
                    return null;
                }
                else{
                    System.out.println("Found value for " + dataName + " in AutoMapping");
                    return expectedDataType.cast(aValue);
                }
            }

            return expectedDataType.cast(aValue);
        }

        if (expectedDataType.isAssignableFrom(theExpectedDataDefinition.getType())) {
            Object aValue = dataValues.get(dataName);
            if (aValue == null) {//if value is null, try to get it from auto mapping
                String[] split = dataName.split("\\.");//to get actual output name for auto mapping
                aValue = AutoMappingMap.get(new AutoMapping(expectedDataType,split[1]));
                if(aValue == null) {
                    System.out.println("Didnt find value for " + dataName + " in AutoMapping");
                    return null;
                }
                else{
                    System.out.println("Found value for " + dataName + " in AutoMapping");
                    return expectedDataType.cast(aValue);
                }
            }
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

        String customMapping = getCustomMapping(dataName);
        String[] split = dataName.split("\\.");//to get actual output name for auto mapping

        // we have the DD type, so we can make sure that its from the same type
        if (theData.getType().isAssignableFrom(value.getClass())) {
            dataValues.put(customMapping, value);
            AutoMappingMap.put(new AutoMapping(theData.getType(),split[1]),value);
            return true;
        } else {
            // error handling of some sort...
        }

        return false;
    }

    @Override
    public void addStep(String key, Object value,DataDefinition dataDefinition, String alias, String customMapping)
    {

        if (alias != null){
            dataValues.put(alias, value);
            dataDefinitions.put(alias,dataDefinition);
            FlowLevelAliases.put(key, alias);
        }
        else {
            dataValues.put(key, value);
            dataDefinitions.put(key,dataDefinition);
            FlowLevelAliases.put(key, key);
        }
        if (customMapping != null && alias != null)
            CustomMapping.put(alias,customMapping);
        else if (customMapping != null)
            CustomMapping.put(key,customMapping);
        else if (alias!=null){
            CustomMapping.put(alias,alias);
        } else {
            CustomMapping.put(key,key);
        }
    }

    @Override
    public void addStepAlias(String key, String stepAlias, boolean skipIfFail)
    {//TODO: check why skipIfFail was wrong + how to actually use it
        if(stepAliases.containsKey(key))
            stepAliases.get(key).add(new stepAliasing(stepAlias,skipIfFail));
        else {
            Queue<stepAliasing> queue = new LinkedList<>();
            queue.add(new stepAliasing(stepAlias,skipIfFail));
            stepAliases.put(key,queue);
        }
    }

    @Override
    public String getAlias(String key,Class<?> expectedDataType)
    {
        for(String CKeys : CustomMapping.keySet())
        {
            if(CustomMapping.get(CKeys).equals(key))
                return CKeys;
        }
        for(AutoMapping autoMapping : AutoMappingMap.keySet())
        {
            boolean flag = true;
            String tmp[] = key.split("\\.");
            /*for (String str : flowDef.getFlowFreeOutputsString()){
                String tmp2[] = str.split("\\.");
                if(tmp2[0].equals(tmp[0])){
                    if (FlowLevelAliases.get(key) == str) // Check if it is free output that means he doesnt need aliasing
                        flag = false;
                }

            }*/
            if(autoMapping.getName().equals(tmp[1])&&autoMapping.getType().equals(expectedDataType) && flag)
                return key;
        }
        if(FlowLevelAliases.containsKey(key))
            return FlowLevelAliases.get(key);
        else {
            Queue<stepAliasing> queue = stepAliases.get(key);
            return queue.poll().getStepAlias();
        }
    }

    @Override
    public String getStepAlias(String key)
    {
        if(stepAliases.containsKey(key))
            return stepAliases.get(key).poll().getStepAlias();
        else {
            System.out.println("No alias for " + key);
            return "Fake step";
        }
    }


    @Override
    public Object dataValueReturn(String key) {
        return dataValues.get(key);
    }

    @Override
    public String getCustomMapping(String key)
    {
        String result = CustomMapping.get(key);
        if (result != null)
            return result;
        else
            return key;
    }


}
