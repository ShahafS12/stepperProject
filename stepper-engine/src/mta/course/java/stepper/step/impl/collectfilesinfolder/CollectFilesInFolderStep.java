package mta.course.java.stepper.step.impl.collectfilesinfolder;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep()
    {
        super("CollectFilesInFolderStep", true);
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.DOUBLE));//change later if needed
    }
    public StepResult invoke(StepExecutionContext context)
    {
        // fetch inputs here, somehow


        // do some complex logic...

        // add outputs here, somehow

        // through the context, as part of writing the step's logic I should be able to:
        // 1. add log lines
        // 2. add summary line

        // return result
        return StepResult.SUCCESS;
    }
}
