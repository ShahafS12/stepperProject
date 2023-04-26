package mta.course.java.stepper.flow.definition.api;

import org.w3c.dom.NodeList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlowValidator
{
    private final FlowDefinition flowDefinition;

    public FlowValidator(FlowDefinition flowDefinition)
    {
        this.flowDefinition = flowDefinition;
    }

    public static void validate(){

    }
    private boolean nonExistingStep(){
        List<StepUsageDeclaration> steps = flowDefinition.getFlowSteps();
        for (StepUsageDeclaration step : steps) {
            if (step == null) {
                return true;
            }
        }//might not work because of the way we are adding steps from the xml
        return false;
    }
    private boolean duplicateOutputNames(){
        List<String> outputs = flowDefinition.getFlowFormalOutputs();
        Set<String> set = new HashSet<>(outputs);
        return set.size() < outputs.size();
    }
    private boolean hasNonUserFriendlyMandatoryInputs(){
        return false;
    }
    private boolean invalidCustomMapping(){
        return false;
    }
    private boolean invalidAlias(){
        //to a step/data that doesn't exist in the flow
        return false;
    }
    private boolean invalidManualInputs(){
        //multiple inputs with the same name but different types
        return false;
    }

}
