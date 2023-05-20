package mta.course.java.stepper.dd.impl.Enumerator;

import mta.course.java.stepper.dd.api.AbstractDataDefinition;

public class EnumeratorDataDefinition extends AbstractDataDefinition
{
    public EnumeratorDataDefinition()  {super("Enumerator", true, Enum.class);}
    @Override
    public Enumerator getValue(String name){
        return new Enumerator();
    }
}
