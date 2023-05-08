package mta.course.java.stepper.dd.impl.list;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

import java.util.ArrayList;

public class ListDataDefenition extends AbstractDataDefinition
{
    public ListDataDefenition()  {
        super("List", false, ArrayList.class);
    }
    public <T> T getValue(String name){
        return null;
    }
}
