package mta.course.java.stepper.step.impl.ToJsonStep;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;

public class ToJsonStep extends AbstractStepDefinition {
    public ToJsonStep() {
        super("To Json", true);
        addInput(new DataDefinitionDeclarationImpl("CONTENT", DataNecessity.MANDATORY, "Content", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.NA, "Json representation", DataDefinitionRegistry.JSON));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        String content = context.getDataValue(context.getAlias(finalStepName + "." + "CONTENT", String.class), String.class);

        try {
            Gson gson = new Gson();
            JsonObject jsonString = gson.fromJson(content, JsonObject.class);
            String beforeStarting = "Content is JSON string. Converting it to JSON";
            context.addLogLine(finalStepName, beforeStarting);
            context.storeDataValue(context.getAlias(finalStepName+"."+"JSON", JsonObject.class), jsonString);
            return StepResult.SUCCESS;
        } catch (Exception e) {
            String beforeStarting = "Content is not a valid JSON representation.";
            context.addLogLine(finalStepName, beforeStarting);
            context.addSummaryLine(finalStepName, e.getMessage());
            return StepResult.FAILURE;
        }
    }
}
