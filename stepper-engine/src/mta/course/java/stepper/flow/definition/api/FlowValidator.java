package mta.course.java.stepper.flow.definition.api;

import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataNecessity;
import org.w3c.dom.NodeList;

import java.util.*;

public class FlowValidator
{
    private final FlowDefinition flowDefinition;

    public FlowValidator(FlowDefinition flowDefinition)
    {
        this.flowDefinition = flowDefinition;
    }

    public boolean validate(){
        //validate the flow
        if(nonExistingStep() || duplicateOutputNames() || hasNonUserFriendlyMandatoryInputs() || invalidCustomMapping()||invalidAlias()||invalidManualInputs()){
            return false;
        }
        return true;
    }
    private boolean nonExistingStep(){
        List<StepUsageDeclaration> steps = flowDefinition.getFlowSteps();
        for (StepUsageDeclaration step : steps) {
            if (step == null) {
                System.out.println("The flow contains a non-existing step");
                return true;
            }
        }//might not work because of the way we are adding steps from the xml
        return false;
    }
    private boolean duplicateOutputNames(){
        List<String> outputs = flowDefinition.getFlowFreeOutputsString();
        Set<String> set = new HashSet<>(outputs);
        if(set.size() < outputs.size()){
            System.out.println("The flow contains duplicate output names");
        }
        return set.size() < outputs.size();
    }
    private boolean hasNonUserFriendlyMandatoryInputs(){
        for(DataDefinitionDeclaration input : flowDefinition.getFlowFreeInputs()){
            if (input.necessity()== DataNecessity.MANDATORY && !input.dataDefinition().isUserFriendly()){
                System.out.println("The flow contains non-user-friendly mandatory inputs");
                return true;
            }
        }
        return false;
    }
    private boolean invalidCustomMapping(){
        Map<String,String> customMapping = flowDefinition.getCustomMapping();
        Map<String,Class<?>> allInputs = flowDefinition.getAllInputs();
        Map<String,Class<?>> allOutputs = flowDefinition.getAllOutputs();
        List<String> stepNames = flowDefinition.getFinalStepNames();
        for (Map.Entry<String, String> entry : customMapping.entrySet()) {
            String from = entry.getKey();
            String to = entry.getValue();
            String[] fromParts = from.split("\\.");
            String[] toParts = to.split("\\.");
            if(!allOutputs.containsKey(from))
            {
                System.out.println("The flow contains a custom mapping with a non-existing output");
                return true;
            }
            if(!allInputs.containsKey(to))
            {
                System.out.println("The flow contains a custom mapping with a non-existing input");
                return true;
            }
            int fromStepIndex = stepNames.indexOf(fromParts[0]);
            int toStepIndex = stepNames.indexOf(toParts[0]);
            if(fromStepIndex > toStepIndex)
            {
                System.out.println("The flow contains a custom mapping with invalid order");
                return true;
            }
            Class<?> fromType = allOutputs.get(from);
            Class<?> toType = allInputs.get(to);
            if(!fromType.equals(toType))
            {
                System.out.println("The flow contains a custom mapping with invalid types");
                return true;
            }
        }
        return false;
    }
    private boolean invalidAlias(){
        //to a step/data that doesn't exist in the flow
        Map<String,String> flowLevelAlias = flowDefinition.getAllFlowLevelAlias();
        List<StepUsageDeclaration> steps = flowDefinition.getFlowSteps();
        for (String key : flowLevelAlias.keySet()) {
            String[] parts = key.split("\\.");
            for(StepUsageDeclaration step : steps) {
                if (step.getStepName().equals(parts[0])) {
                    List<DataDefinitionDeclaration> inputsAndOutputs = step.getStepDefinition().inputs();
                    inputsAndOutputs.addAll(step.getStepDefinition().outputs());
                    for (DataDefinitionDeclaration dd : inputsAndOutputs) {
                        if (dd.dataDefinition().getName().equals(parts[1])) {
                            System.out.println("The flow contains an alias to a non-existing step/data");
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    private boolean invalidManualInputs(){
        //multiple inputs with the same name but different types
        List<DataDefinitionDeclaration> flowFreeDD = flowDefinition.getFlowFreeInputs();
        List<String> flowFreeDDNames = flowDefinition.getFlowFreeInputsString();
        Map<String,Class<?>> inputTypeMap = new HashMap<>();
        for (DataDefinitionDeclaration dd : flowFreeDD) {
            if (inputTypeMap.containsKey(dd.dataDefinition().getName())) {
                if (!inputTypeMap.get(dd.dataDefinition().getName()).equals(dd.dataDefinition().getType())) {
                    System.out.println("The flow contains multiple inputs with the same name but different types");
                    return true;
                }
            }
            else {
                inputTypeMap.put(dd.dataDefinition().getName(), dd.dataDefinition().getType());
            }
        }

        return false;

    }

}
