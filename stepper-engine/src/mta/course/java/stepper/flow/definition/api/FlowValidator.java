package mta.course.java.stepper.flow.definition.api;

import org.w3c.dom.NodeList;

import java.util.HashSet;
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
    private boolean duplicateStepNames(){
        return false;
    }
    private boolean duplicateOutputNames(){
        return false;
    }
    private boolean stepNameExists(){
        return false;
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
