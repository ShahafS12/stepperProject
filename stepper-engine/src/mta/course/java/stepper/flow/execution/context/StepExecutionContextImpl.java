package mta.course.java.stepper.flow.execution.context;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.string.StringDataDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;

import java.io.Serializable;
import java.sql.Time;
import java.time.Instant;
import java.util.*;

public class StepExecutionContextImpl implements StepExecutionContext, Serializable
{

    private Map<String, Object> dataValues;
    private Map<String,DataDefinition> dataDefinitions;
    private Map<String, ArrayList<String>> logs;
    private Map<String, String> summaryLines;
    private Map<String, String> FlowLevelAliases;
    private Map<String, String> CustomMapping;
    private Map<AutoMapping,Object> AutoMappingMap;
    private Map<String, Queue<stepAliasing>> stepAliases;
    private final Map<String,String> flowFreeInputs;
    private FlowDefinition flowDef;


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
    public StepExecutionContextImpl(){
        dataValues = new HashMap<>();
        dataDefinitions = new HashMap<>();
        logs = new HashMap<>();
        summaryLines = new HashMap<>();
        FlowLevelAliases = new HashMap<>();
        CustomMapping = new HashMap<>();
        AutoMappingMap = new HashMap<>();
        stepAliases = new HashMap<>();
        flowFreeInputs = new HashMap<>();
        flowDef = null;
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
    public Map<String, ArrayList<String>> getLogs()
    {
        return logs;
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
            for (String str : flowDef.getFlowFreeOutputsString()){
                String tmp2[] = str.split("\\.");
                if(tmp2[0].equals(tmp[0])){
                    if (FlowLevelAliases.get(key) == str) // Check if it is free output that means he doesnt need aliasing
                        flag = false;
                }

            }
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
    public void addFlowLevelAlias(String key, String alias, Class<?> theExpectedDataType) {
        if (!FlowLevelAliases.containsKey(key)) {
            FlowLevelAliases.put(key, alias);
            DataDefinitionDeclarationImpl dd = new DataDefinitionDeclarationImpl(alias, DataNecessity.NA, "" ,getDataDefinitionRegistry(theExpectedDataType));
            dataDefinitions.put(alias,dd.dataDefinition());
        }
    }

    @Override
    public DataDefinitionRegistry getDataDefinitionRegistry(Class<?> theExpectedDataType) {
        if (theExpectedDataType.getName().equals("java.lang.String"))
            return DataDefinitionRegistry.STRING;
        else if (theExpectedDataType.getName().equals("java.lang.Double"))
            return DataDefinitionRegistry.DOUBLE;
        else if (theExpectedDataType.getName().equals("java.lang.Relation"))
            return DataDefinitionRegistry.RELATION;
        else if (theExpectedDataType.getName().equals("java.lang.List"))
            return DataDefinitionRegistry.LIST;
        else if (theExpectedDataType.getName().equals("java.lang.Map"))
            return DataDefinitionRegistry.MAP;
        else if (theExpectedDataType.getName().equals("java.lang.Number"))
            return DataDefinitionRegistry.Number;
        else if (theExpectedDataType.getName().equals("com.google.gson.JsonObject"))
            return DataDefinitionRegistry.JSON;
        else if (theExpectedDataType.getName().equals("java.lang.Enum"))
            return DataDefinitionRegistry.Enumeration;
    return null;
    }

    @Override
    public Map<String, DataDefinition> getDataDefinitions()
    {
        return dataDefinitions;
    }

    @Override
    public Map<String, String> getSummaryLines()
    {
        return summaryLines ;
    }

    @Override
    public Map<String, String> getFlowLevelAliasing()
    {
        return FlowLevelAliases;
    }

    @Override
    public Map<String, String> getCostumeMapping()
    {
        return CustomMapping;
    }

    @Override
    public Map<AutoMapping, Object> getAutoMapping()
    {
        return AutoMappingMap;
    }

    @Override
    public Map<String, Queue<stepAliasing>> getStepLevelAliasing()
    {
        return stepAliases;
    }

    @Override
    public FlowDefinition getFlowDefinition()
    {
        return flowDef;
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


    public void setDataValuesMap(Map<String, Object> o)
    {
        dataValues = o;
    }

    public void setDataDefinitions(Map<String, DataDefinition> o)
    {
        dataDefinitions = o;
    }

    public void setLogs(Map<String, ArrayList<String>> o)
    {
        logs = o;
    }

    public void setSummaryLines(Map<String, String> o)
    {
        summaryLines = o;
    }

    public void setFlowLevelAliasing(Map<String, String> o)
    {
        FlowLevelAliases = o;
    }

    public void setCostumeMapping(Map<String, String> o)
    {
        CustomMapping = o;
    }

    public void setAutoMapping(Map<AutoMapping,Object> o)
    {
        AutoMappingMap = o;
    }

    public void setStepLevelAliasing(Map<String, Queue<stepAliasing>> o)
    {
        stepAliases = o;
    }

    public void setFlowDefinition(FlowDefinition o)
    {
        flowDef =  o;
    }
}
