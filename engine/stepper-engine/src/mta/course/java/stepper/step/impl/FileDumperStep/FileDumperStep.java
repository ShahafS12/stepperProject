package mta.course.java.stepper.step.impl.FileDumperStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.FileWriter;
import java.io.IOException;

public class FileDumperStep extends AbstractStepDefinition {
    public FileDumperStep () {
        super("File Dumper", true);
        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("FILE_NAME", DataNecessity.MANDATORY, "Target file path", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "File Creation Result", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        String content = context.getDataValue(context.getAlias(finalStepName+"."+"CONTENT",String.class), String.class);
        String fileName = context.getDataValue(context.getAlias(finalStepName+"."+"FILE_NAME",String.class), String.class);

        String beforeWriting = "About to create file named "+ fileName;
        context.addLogLine(finalStepName, beforeWriting);

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
            String afterWritingSummary = "Finish writing successfully";
            context.addSummaryLine(finalStepName,afterWritingSummary);
        }
        catch (IOException e){
            String failedWriting = "Failed writing the file: " + fileName;
            context.addLogLine(finalStepName, failedWriting);
            context.addSummaryLine(finalStepName, failedWriting);
            return StepResult.FAILURE;
        }

        context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT",String.class), content);

        if (content == "") {
            String emptyFile = "The content was empty, created empty file!";
            context.addLogLine(finalStepName,emptyFile);
            return StepResult.WARNING;
        }

        return StepResult.SUCCESS;
    }
}
