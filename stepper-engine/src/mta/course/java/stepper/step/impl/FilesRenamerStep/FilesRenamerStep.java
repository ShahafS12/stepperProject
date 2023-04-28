package mta.course.java.stepper.step.impl.FilesRenamerStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.util.ArrayList;

public class FilesRenamerStep extends AbstractStepDefinition {
    public FilesRenamerStep() {
        super("Files Renamer", false);
        addInput(new DataDefinitionDeclarationImpl("FILES_TO_RENAME", DataNecessity.MANDATORY, "Files to rename", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("PREFIX", DataNecessity.OPTIONAL, "Add this prefix", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("SUFFIX", DataNecessity.OPTIONAL, "Addend this suffix", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RENAME_RESULT", DataNecessity.NA, "Rename operation summary", DataDefinitionRegistry.RELATION));
    }

    public StepResult invoke(StepExecutionContext context) {
        ArrayList<File> filesRename = context.getDataValue(context.getAlias(this.name()+"."+"FILES_TO_RENAME"), ArrayList.class);
        String prefix = context.getDataValue(context.getAlias(this.name()+"."+"PREFIX"), String.class);
        String suffix = context.getDataValue(context.getAlias(this.name()+"."+"SUFFIX"), String.class);

        ArrayList<String> renameColumns = new ArrayList<String>();
        renameColumns.add("No.");
        renameColumns.add("Original File Name");
        renameColumns.add("Changed File Name");
        RelationData renameTable = new RelationData(renameColumns);

        if (filesRename.isEmpty()){
            String emptyListSummary = "The list of files is empty";
            context.addSummaryLine("FilesRenamerStep", emptyListSummary);
            return StepResult.SUCCESS;
        }
        ArrayList<String> failedFileArray = new ArrayList<>();
        String changeFileLog = "About to start rename " + filesRename.size() + " files. Adding prefix: "+ prefix + "; adding suffix: " +suffix;
        context.addLogLine("FilesRenamerStep",changeFileLog );
        int counter =1;
        for (File file:filesRename){
            String newName;
            // Checking if optional as requested
            if (prefix.equals("") && (!suffix.equals(""))) {
                newName =  file.getName() + suffix;
            } else if ((!prefix.equals("")) && suffix.equals("")) {
                newName =  prefix + file.getName();
            } else if (prefix.equals("") && suffix.equals("")) {
                newName =  file.getName();
            } else {
                newName = prefix + file.getName() + suffix;
            }
            File newFile = new File(file.getParent(), newName);
            if(file.renameTo(newFile)){
                ArrayList<String> tmp = new ArrayList<String>();
                tmp.add(String.valueOf(counter));
                tmp.add(file.getName());
                tmp.add(newFile.getName());
                renameTable.addRow(tmp);
                counter++;
            } else { // failed renaming the file
                String failedFile= file.getName();
                failedFileArray.add(failedFile);
                String failedFileLog = "Problem renaming file: " + file.getName();
                context.addLogLine("FilesRenamerStep",failedFileLog );
            }
        }


        context.storeDataValue(context.getAlias(this.name()+"."+"RENAME_RESULT"), renameTable);

        // One or more of the files failed!
        if (failedFileArray.size() >= 1 ){
            String failedFilesNames = "The name of the failed files are: ";
            for (String file : failedFileArray){
                failedFilesNames += file + " ";
            }
            context.addSummaryLine("FilesRenamerStep", failedFilesNames);
            return StepResult.WARNING;
        }

        return StepResult.SUCCESS;
    }
}
