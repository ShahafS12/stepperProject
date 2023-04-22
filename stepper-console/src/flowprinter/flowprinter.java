package flowprinter;

import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;

public class flowprinter
{
    public void printFlow(FlowDefinitionImpl flow)
    {
        System.out.println("Flow name: " + flow.getName());
        System.out.println("Flow description: " + flow.getDescription());
        System.out.println("Flow steps: " + flow.getFlowSteps());
        System.out.println("Flow formal outputs: " + flow.getFlowFormalOutputs());
        System.out.println("Flow free inputs: " + flow.getFlowFreeInputs());
        //might need to add toString() for some of the above
    }
}
