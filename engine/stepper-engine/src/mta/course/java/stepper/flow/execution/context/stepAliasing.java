package mta.course.java.stepper.flow.execution.context;

public class stepAliasing
{
    private final String stepAlias;
    private final boolean skipIfFail;
    public stepAliasing(String stepAlias, boolean skipIfFail)
    {
        this.stepAlias = stepAlias;
        this.skipIfFail = skipIfFail;
    }
    public String getStepAlias()
    {
        return stepAlias;
    }
    public boolean isSkipIfFail()
    {
        return skipIfFail;
    }
}
