package mta.course.java.stepper.step.impl.JsonDataExtractorStep;

import com.google.gson.JsonElement;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;
import com.jayway.jsonpath.JsonPath;

public class JsonDataExtractorStep extends AbstractStepDefinition {
    public JsonDataExtractorStep() {
        super("Json Data Extractor", true);
        addInput(new DataDefinitionDeclarationImpl("JSON", DataNecessity.MANDATORY, "Json source", DataDefinitionRegistry.JSON));
        addInput(new DataDefinitionDeclarationImpl("JSON_PATH", DataNecessity.MANDATORY, "Data", DataDefinitionRegistry.STRING));

        addOutput(new DataDefinitionDeclarationImpl("VALUE", DataNecessity.NA, "Data value", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        String json = context.getDataValue(context.getAlias(finalStepName + "." + "JSON", JsonElement.class), String.class);
        String jsonPath = context.getDataValue(context.getAlias(finalStepName + "." + "JSON_PATH", String.class), String.class);
        try {
            String value = JsonPath.read(json, jsonPath);
            String beforeStarting = "Extracting data:" + jsonPath + " Value:" + value;
            context.addLogLine(finalStepName, beforeStarting);
            context.storeDataValue(context.getAlias(finalStepName+"."+"VALUE", String.class), value);
            return StepResult.SUCCESS;
        } catch (Exception e) {
            String beforeStarting = "No value found for json path: " + jsonPath;
            context.addLogLine(finalStepName, beforeStarting);
            context.addSummaryLine(finalStepName, e.getMessage());
            return StepResult.FAILURE;
        }
    }

}
