package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.STContinuation;
import dataloader.generated.STContinuationMapping;

import java.util.List;
import java.util.Map;

public class Continuation
{
    private String targetFlow;
    private Map<String, List<String>> continuationMapping;
    public Continuation(String targetFlow, Map<String,List<String>> continuationMapping)
    {
        this.targetFlow = targetFlow;
        this.continuationMapping = continuationMapping;
    }
    public Continuation(STContinuation stContinuation){
        this.continuationMapping = new java.util.HashMap<>();
        this.targetFlow = stContinuation.getTargetFlow();
        for(STContinuationMapping stContinuationMap : stContinuation.getSTContinuationMapping()){
            if(this.continuationMapping.containsKey(stContinuationMap.getSourceData())){
                this.continuationMapping.get(stContinuationMap.getSourceData()).add(stContinuationMap.getTargetData());
            }
            else{
                this.continuationMapping.put(stContinuationMap.getSourceData(),new java.util.ArrayList<>());
                this.continuationMapping.get(stContinuationMap.getSourceData()).add(stContinuationMap.getTargetData());
            }
        }
    }
    public String getTargetFlow()
    {
        return targetFlow;
    }
    public Map<String,List<String>> getContinuationMapping()
    {
        return continuationMapping;
    }
}
