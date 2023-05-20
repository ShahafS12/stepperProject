package mta.course.java.stepper.step.impl.CommandLineStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CommandLineStep extends AbstractStepDefinition {
    public CommandLineStep() {
        super("Command Line", false);
        addInput(new DataDefinitionDeclarationImpl("COMMAND", DataNecessity.MANDATORY, "Command", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ARGUMENTS", DataNecessity.OPTIONAL, "Command arguments", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("RESULT", DataNecessity.NA, "Command output", DataDefinitionRegistry.STRING));
    }

    public StepResult invoke(StepExecutionContext context) {

        String finalStepName = context.getStepAlias(this.name());
        String command = context.getDataValue(context.getAlias(finalStepName + "." + "COMMAND", String.class), String.class);
        String arguments = context.getDataValue(context.getAlias(finalStepName + "." + "ARGUMENTS", String.class), String.class);

        String beforeStarting = "About to invoke " + command + " " + arguments;
        context.addLogLine(finalStepName, beforeStarting);

        try {
            String output = runCommand(command, arguments);
            context.storeDataValue(context.getAlias(finalStepName+"."+"RESULT", String.class), output);
        }
        catch (Exception e){}

        return StepResult.SUCCESS;
    }
    public static String runCommand(String command, String arguments) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command, arguments);
        Process process = processBuilder.start();

        // Read the output of the command
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();

        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        // Wait for the command to finish
        int exitCode = process.waitFor();

        if (exitCode == 0) {
            return output.toString();
        } else {
            throw new RuntimeException("Command execution failed with exit code: " + exitCode);
        }
    }



}