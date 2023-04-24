package mta.course.java.stepper.step.impl.FilesContentExtractorStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.util.ArrayList;

public class FilesContentExtractorStep extends AbstractStepDefinition {
    public FilesContentExtractorStep(){
        super("FilesContentExtractorStep", true);
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to extract", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY, "Line number to extract", DataDefinitionRegistry.DOUBLE)); // TODO: CHANGE TO NUMBER

        addOutput(new DataDefinitionDeclarationImpl("DATA", DataNecessity.NA, "Data extraction", DataDefinitionRegistry.RELATION));
    }
    public StepResult invoke(StepExecutionContext context){
        ArrayList<File> filesList = context.getDataValue("FILES_LIST", ArrayList.class);

        // TODO: I should finish
        return StepResult.SUCCESS;
    }
}
