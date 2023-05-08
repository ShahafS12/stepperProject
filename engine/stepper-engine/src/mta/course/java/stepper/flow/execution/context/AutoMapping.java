package mta.course.java.stepper.flow.execution.context;

public class AutoMapping
{
    private Class<?> type;
    private String name;
    public AutoMapping(Class<?> type, String name)
    {
        this.type = type;
        this.name = name;
    }
    public Class<?> getType()
    {
        return type;
    }
    public String getName()
    {
        return name;
    }
    @Override
    public boolean equals(Object obj)
    {
        if(obj instanceof AutoMapping)
        {
            AutoMapping autoMapping = (AutoMapping)obj;
            return autoMapping.getName().equals(name) && autoMapping.getType().equals(type);
        }
        return false;
    }
    @Override
    public int hashCode() {
        return name.hashCode() + type.hashCode();
    }
}
