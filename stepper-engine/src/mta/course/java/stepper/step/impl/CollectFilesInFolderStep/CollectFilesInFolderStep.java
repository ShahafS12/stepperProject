package mta.course.java.stepper.step.impl.CollectFilesInFolderStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep()
    {
        super("Collect Files In Folder", true);
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.Number));
    }
    public StepResult invoke(StepExecutionContext context)
    {
        // fetch inputs here, somehow
        String finalStepName = context.getStepAlias(this.name());
        String folderName = context.getDataValue(context.getAlias(finalStepName+"."+"FOLDER_NAME",String.class), String.class);
        String filter = context.getDataValue(context.getAlias(finalStepName+"."+"FILTER",String.class), String.class);

        // do some complex logic...
        String beforeReading = "Reading folder " + folderName + " content with filter " + filter;
        context.addLogLine(finalStepName, beforeReading);
        // add outputs here, somehow


        File folder = new File (folderName);


        String failedFolder;
        if (!folder.exists()){
            failedFolder = "The provided path: "+ folderName +" is not exists";
            context.addLogLine(finalStepName, failedFolder);
            context.addSummaryLine(finalStepName, failedFolder);
            return StepResult.FAILURE;

        } else if (!folder.isDirectory()) {
            failedFolder = "The provided path: "+ folderName + " is not a directory";
            context.addLogLine(finalStepName, failedFolder);
            context.addSummaryLine(finalStepName, failedFolder);
            return StepResult.FAILURE;
        }

        File[] files;

        if (!filter.equals("")){ // OPTIONAL
            FilenameFilter fileFilter = new FilenameFilter() {
                @Override
                public boolean accept(File folder, String fileName) {
                    return fileName.endsWith(filter);
                }
            };
            files = folder.listFiles(fileFilter); // output 1 - FilesList
        }
        else {
            files = folder.listFiles();
        }

        int totalFilesFound = files.length; // output 2 - Files Len

        List<File> filesList = Arrays.asList(files);
        ArrayList<File> filesArrayList = new ArrayList<>(filesList);

        context.storeDataValue(context.getAlias(finalStepName+"."+"FILES_LIST",ArrayList.class), filesArrayList);
        context.storeDataValue(context.getAlias(finalStepName+"."+"TOTAL_FOUND", int.class), totalFilesFound);


        String afterReading = "Found " + totalFilesFound + " files in folder matching the filter";
        context.addLogLine(finalStepName, afterReading);

        if (totalFilesFound == 0){
            context.addSummaryLine(finalStepName, "The folder had no files");
            return StepResult.WARNING;
        }

        context.addSummaryLine(finalStepName, afterReading);
        return StepResult.SUCCESS;
    }


}
