package mta.course.java.stepper.step.impl.FilesRenamerStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.util.ArrayList;

public class FilesRenamerStep extends AbstractStepDefinition {
    public FilesRenamerStep() {
        super("FilesRenamerStep", false);
        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY, "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL, "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL, "Addend this suffix", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA, "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    public StepResult invoke(StepExecutionContext context) {
        ArrayList<File> filesRename = context.getDataValue("FILES_TO_RENAME", ArrayList.class);
        String Prefix = context.getDataValue("PREFIX", String.class);
        String Suffix = context.getDataValue("SUFFIX", String.class);

        if (filesRename.isEmpty()){
            String emptyList = "The list of files is empty"; //TODO: Summary list
            return StepResult.SUCCESS;
        }
        ArrayList<String> failedFileArray = new ArrayList<>();
        for (File file:filesRename){
            String newName = Prefix + file.getName() + Suffix;
            File newFile = new File(file.getParent(), newName);
            if(file.renameTo(newFile)){

            } else { // failed renaming the file
                String failedFile= file.getName();
                failedFileArray.add(failedFile);
            }
        }

        if (failedFileArray.size() >= 1 ){

            return StepResult.WARNING;
        }

        return StepResult.SUCCESS;
    }
}
