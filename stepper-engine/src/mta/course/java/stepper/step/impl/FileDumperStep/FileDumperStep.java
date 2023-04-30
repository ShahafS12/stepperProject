package mta.course.java.stepper.step.impl.FileDumperStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.BufferedWriter;
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

        String content = context.getDataValue(context.getAlias(this.name()+"."+"CONTENT"), String.class);
        String fileName = context.getDataValue(context.getAlias(this.name()+"."+"FILE_NAME"), String.class);

        String beforeWriting = "About to create file named "+ fileName;
        context.addLogLine("FileDumper", beforeWriting);

        try {
            FileWriter writer = new FileWriter(fileName);
            writer.write(content);
            writer.close();
            String afterWritingSummary = "Finish writing successfully";
            context.addSummaryLine("FileDumper",afterWritingSummary);
        }
        catch (IOException e){
            String failedWriting = "Failed writing the file: " + fileName; // TODO: Why failed! Dont understand the instrunctions
            context.addLogLine("FileDumper", failedWriting);
            context.addSummaryLine("FileDumper", failedWriting);
            return StepResult.FAILURE;
        }

        context.storeDataValue(context.getAlias(this.name()+"."+"RESULT"), content); // TODO: dont really understand what should be in result

        if (content == "") {
            String emptyFile = "The content was empty, created empty file!";
            context.addLogLine("FileDumper",emptyFile);
            return StepResult.WARNING;
        }

        return StepResult.SUCCESS;
    }
}
