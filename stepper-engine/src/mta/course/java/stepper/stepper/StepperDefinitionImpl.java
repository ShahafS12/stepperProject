package mta.course.java.stepper.stepper;

import dataloader.generated.STFlow;
import dataloader.generated.STFlows;
import dataloader.generated.STStepper;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;

import java.util.ArrayList;
import java.util.List;

public class StepperDefinitionImpl implements StepperDefinition
{
    private List<FlowDefinition> flows;

    public StepperDefinitionImpl(List<FlowDefinition> flows)
    {
        this.flows = flows;
    }
    public StepperDefinitionImpl(){
        flows = new ArrayList<>();
    }
    public StepperDefinitionImpl(STStepper stepper){
        flows = new ArrayList<>();
        STFlows stFlows = stepper.getSTFlows();
        List<STFlow> stFlowList = stFlows.getSTFlow();
        for (int i = 0; i < stFlowList.size(); i++) {
            flows.add(new FlowDefinitionImpl(stFlowList.get(i)));
        }
    }
    @Override
    public void addFlow(FlowDefinition flow){
        flows.add(flow);
    }

    @Override
    public List<String> getFlowNames()
    {
        return null;
    }

    @Override
    public List<String> getFlowFormalInputs(String flowName)
    {
        return null;
    }

    @Override
    public List<String> getFlowFormalOutputs(String flowName)
    {
        return null;
    }

}
