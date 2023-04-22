package mta.course.java.stepper.dd.impl.list;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.relation.RelationData;

import java.util.ArrayList;

public class ListDataDefenition extends AbstractDataDefinition
{
    public ListDataDefenition()  {
        super("List", false, ArrayList.class);
    }
}
