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
    private ArrayList<FlowDefinition> flows;
    private int maxThreads;

    public StepperDefinitionImpl(ArrayList<FlowDefinition> flows)
    {
        this.flows = flows;
    }
    public StepperDefinitionImpl(){
        flows = new ArrayList<>();
    }
    public StepperDefinitionImpl(STStepper stepper){
        try {
            maxThreads = stepper.getSTThreadPool();
            flows = new ArrayList<>();
            STFlows stFlows = stepper.getSTFlows();
            //check if there are multiple flows with same name
            for(STFlow flow: stFlows.getSTFlow()){
                for(STFlow flow2: stFlows.getSTFlow()){
                    if(flow.getName().equals(flow2.getName()) && flow != flow2){
                        throw new RuntimeException("Error initializing stepper definition: multiple flows with same name");
                    }
                }
            }
            List<STFlow> stFlowList = stFlows.getSTFlow();
            for (int i = 0; i < stFlowList.size(); i++) {
                flows.add(new FlowDefinitionImpl(stFlowList.get(i)));
            }
        }
        catch (RuntimeException e){
            throw new RuntimeException("Error initializing stepper definition", e);
        }
    }
    @Override
    public void addFlow(FlowDefinition flow){
        flows.add(flow);
    }

    @Override
    public ArrayList<String> getFlowNames()
    {
        ArrayList<String> flowNames = new ArrayList<String>();
        for (int i=0; i< flows.size(); i++){
            flowNames.add(flows.get(i).getName());
        }
        return flowNames;
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
    }//TODO check if needed

    @Override
    public FlowDefinition getFlowDefinition(String flowName)
    {
        for(FlowDefinition flow : flows)
        {
            if(flow.getName().equals(flowName))
            {
                return flow;
            }
        }
        return null;
    }
    public ArrayList<FlowDefinition> getFlows(){
        return flows;
    }

    @Override
    public int getMaxThreads(){
        return maxThreads;
    }

}
