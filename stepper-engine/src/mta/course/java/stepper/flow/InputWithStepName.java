package mta.course.java.stepper.flow;

import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

public class InputWithStepName
{
    private final String stepName;
    private final DataDefinitionDeclaration dataDefinitionDeclaration;
    public InputWithStepName(String stepName, DataDefinitionDeclaration dataDefinitionDeclaration)
    {
        this.stepName = stepName;
        this.dataDefinitionDeclaration = dataDefinitionDeclaration;
    }
    public String getStepName()
    {
        return stepName;
    }
    public DataDefinitionDeclaration getDataDefinitionDeclaration()
    {
        return dataDefinitionDeclaration;
    }
}
