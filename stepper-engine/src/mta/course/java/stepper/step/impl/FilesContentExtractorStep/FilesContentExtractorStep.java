package mta.course.java.stepper.step.impl.FilesContentExtractorStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.dd.impl.relation.RelationData;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class FilesContentExtractorStep extends AbstractStepDefinition {
    public FilesContentExtractorStep(){
        super("Files Content Extractor", true);
        addInput(new DataDefinitionDeclarationImpl("FILES_LIST", DataNecessity.MANDATORY, "Files to extract", DataDefinitionRegistry.LIST));
        addInput(new DataDefinitionDeclarationImpl("LINE", DataNecessity.MANDATORY, "Line number to extract", DataDefinitionRegistry.Number));

        addOutput(new DataDefinitionDeclarationImpl("SOURCE", DataNecessity.NA, "Data extraction", DataDefinitionRegistry.RELATION));
    }

    public StepResult invoke(StepExecutionContext context){
        String finalStepName = context.getStepAlias(this.name());
        ArrayList<File> filesList = context.getDataValue(context.getAlias(finalStepName+"."+"FILES_LIST",ArrayList.class), ArrayList.class);
        int line = (int) context.getDataValue(context.getAlias(finalStepName+"."+"LINE",Number.class), Number.class);

        ArrayList<String> renameColumns = new ArrayList<String>();
        renameColumns.add("No.");
        renameColumns.add("Original File Name");
        renameColumns.add("Line content");
        RelationData filesTable = new RelationData(renameColumns);

        int counter = 1;

        for (File file : filesList) {
            String beforeLog = "About to start work on file "+ file.getName();
            context.addLogLine("FilesContentExtractor", beforeLog);

            try  (BufferedReader br = new BufferedReader(new FileReader(file))){
                String lineContent;
                int currentLine = 1;
                while ((lineContent = br.readLine()) != null) {
                    if (currentLine == line){
                        ArrayList<String> tmp = new ArrayList<String>();
                        tmp.add(String.valueOf(counter));
                        tmp.add(file.getName());
                        tmp.add(lineContent);
                        filesTable.addRow(tmp);
                        break;
                    }
                    currentLine++;
                }
            }
            catch (IOException e) {
                String problemFileLog = "Problem extracting line number " + line + " from file " + file.getName();
                context.addLogLine("FilesContentExtractor", problemFileLog);
            }
            counter++;
        }

        if (filesList.size() == 0 ){
            String emptyList = "The list of files is empty";
            context.addLogLine("FilesContentExtractor", emptyList);
            context.addSummaryLine("FilesContentExtractor", emptyList);
            return StepResult.SUCCESS;
        }


        context.storeDataValue(context.getAlias(finalStepName+"."+"SOURCE",RelationData.class), filesTable);

        String summaryLine = "Successfully finish extractor line " + line + " from all files provided";
        context.addSummaryLine("FilesContentExtractor", summaryLine);
        return StepResult.SUCCESS;
    }
}
