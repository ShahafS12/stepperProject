package mta.course.java.stepper.dd.impl.mapping;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;
import mta.course.java.stepper.dd.api.DataDefinition;

import java.util.Map;

public class MappingDataDefenition extends AbstractDataDefinition
{
    public MappingDataDefenition()  {
        super("Mapping", false, Map.class);
    }
    public Map<String, DataDefinition> getValue(String name){
        return null;
    }
}
