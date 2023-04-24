package mta.course.java.stepper.step.impl.FilesDeleterStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FilesDeleterStep extends AbstractStepDefinition {
    public FilesDeleterStep() {
        super("CollectFilesInFolderStep", false);
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to delete", DataDefinitionRegistry.LIST));

        addOutput(new DataDefinitionDeclarationImpl("DELETED_LIST", DataNecessity.NA, "Files failed to be deleted", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("DELETION_STAT", DataNecessity.NA, "Deletion summary result", DataDefinitionRegistry.MAP));
    }

    public StepResult invoke(StepExecutionContext context)
    {
        // fetch inputs here, somehow
        ArrayList<File> filesList = context.getDataValue("FILES_LIST", ArrayList.class);
        ArrayList<File> notDeletedList = new ArrayList<File>();
        int deletedSuccess = 0;
        // do some complex logic...
        String beforeDeletingFiles = "About to start delete " + filesList.size() + " files";
        for (File file:filesList){
            if(file.delete()){
                deletedSuccess++;
            } else{
                String failedDeleteFile = "Failed to delete file " + file.getName(); // TODO: WHAT TO DO WITH THIS LOG (IN GENERAL - LOGS) ?
                notDeletedList.add(file);
            }
        }
        // add outputs here, somehow
        HashMap<String, Number> deletionStat = new HashMap<String, Number>();
        if (notDeletedList.isEmpty()){
            deletionStat.put("car", deletedSuccess);
            deletionStat.put("cdr", notDeletedList.size());
            String summaryLineStep = "Deleted all the files that were asked!"; //TODO: adding summary line 
            return StepResult.SUCCESS;
        } else if (deletedSuccess == 0) {
            String summaryLineStep = "Didn't delete any file that were asked!"; //TODO: adding summary line
            return StepResult.FAILURE;
        } else {
            deletionStat.put("car", deletedSuccess);
            deletionStat.put("cdr", notDeletedList.size());
            String summaryLineStep = "Deleted "+ deletedSuccess+ " files and failed with "+notDeletedList.size()+" files!"; //TODO: adding summary line
            return StepResult.WARNING;
        }


    }


}
