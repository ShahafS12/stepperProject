package mta.course.java.stepper.flow.definition.api;

import dataloader.generated.STContinuation;
import dataloader.generated.STContinuationMapping;

import java.util.Map;

public class Continuation
{
    private String targetFlow;
    private Map<String,String> continuationMapping;
    public Continuation(String targetFlow, Map<String,String> continuationMapping)
    {
        this.targetFlow = targetFlow;
        this.continuationMapping = continuationMapping;
    }
    public Continuation(STContinuation stContinuation){
        this.continuationMapping = new java.util.HashMap<>();
        this.targetFlow = stContinuation.getTargetFlow();
        for(STContinuationMapping stContinuationMap : stContinuation.getSTContinuationMapping()){
            this.continuationMapping.put(stContinuationMap.getSourceData(),stContinuationMap.getTargetData());
        }
    }
    public void addContinuationMapping(String key, String value)
    {
        continuationMapping.put(key,value);
    }
    public String getTargetFlow()
    {
        return targetFlow;
    }
    public Map<String,String> getContinuationMapping()
    {
        return continuationMapping;
    }
}
