package mta.course.java.stepper.step.impl.CollectFilesInFolderStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.File;
import java.io.FilenameFilter;

public class CollectFilesInFolderStep extends AbstractStepDefinition {
    public CollectFilesInFolderStep()
    {
        super("CollectFilesInFolder", true);
        addInput(new DataDefinitionDeclarationImpl("FOLDER_NAME", DataNecessity.MANDATORY, "Folder name to scan", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILTER", DataNecessity.OPTIONAL, "Filter only these files", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.NA, "Files list", DataDefinitionRegistry.LIST));
        addOutput(new DataDefinitionDeclarationImpl("TOTAL_FOUND", DataNecessity.NA, "Total files found", DataDefinitionRegistry.DOUBLE));//change later if needed
    }
    public StepResult invoke(StepExecutionContext context)
    {
        // fetch inputs here, somehow
        String folderName = context.getDataValue("FOLDER_NAME", String.class);
        String filter = context.getDataValue("FILTER", String.class); // SHOULD BE OPTIONAL **CHECK IT OUT**

        // do some complex logic...
        String beforeReading = "Reading folder " + folderName + " content with filter " + filter;
        context.addLogLine("CollectFilesInFolder", beforeReading);
        // add outputs here, somehow

        FilenameFilter fileFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return name().endsWith(filter);
            }
        };

        File folder = new File(folderName);
        String failedFolder;
        if (!folder.exists()){
            failedFolder = "The provided path: "+ folderName +" is not exists";
            context.addLogLine("CollectFilesInFolder", failedFolder);
            context.addSummaryLine("CollectFilesInFolder", failedFolder);
            return StepResult.FAILURE;

        } else if (!folder.isDirectory()) {
            failedFolder = "The provided path: "+ folderName + " is not a directory";
            context.addLogLine("CollectFilesInFolder", failedFolder);
            context.addSummaryLine("CollectFilesInFolder", failedFolder);
            return StepResult.FAILURE;
        }

        File[] files = folder.listFiles(fileFilter); // output 1 - FilesList
        int totalFilesFound = files.length; // output 2 - Files Len

        context.storeDataValue("FILES_LIST", files);
        context.storeDataValue("TOTAL_FOUND", totalFilesFound);

        // through the context, as part of writing the step's logic I should be able to:
        // 1. add log lines
        // 2. add summary line

        String afterReading = "Found " + totalFilesFound + " files in folder matching the filter";
        context.addLogLine("CollectFilesInFolder", afterReading);

        if (totalFilesFound == 0){
            context.addSummaryLine("CollectFilesInFolder", "The folder had no files");
            return StepResult.WARNING;
        }

        context.addSummaryLine("CollectFilesInFolder", afterReading);
        return StepResult.SUCCESS;
    }


}
