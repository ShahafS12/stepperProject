package adapters.gsonFactory;

import adapters.StepDefinitionAdapter;
import adapters.StepUsageDeclarationAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;

public class GsonFactory {
    private static Gson GSON;
    private static StepDefinitionAdapter stepDefinitionAdapter;
    private static StepUsageDeclarationAdapter stepUsageDeclarationAdapter;
    private static Gson defaultGson = new Gson();

    private GsonFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Gson getInstance() {
        if (GSON == null) {
            GSON = new GsonBuilder()
                    .registerTypeAdapter(StepDefinition.class, getStepDefinitionAdapter())
                    .registerTypeAdapter(StepUsageDeclaration.class, getStepUsageDeclarationAdapter())
                    .create();
        }
        return GSON;
    }

    public static Gson getDefaultInstance() {
        return defaultGson;
    }

    public static StepDefinitionAdapter getStepDefinitionAdapter() {
        if (stepDefinitionAdapter == null) {
            stepDefinitionAdapter = new StepDefinitionAdapter();
        }
        return stepDefinitionAdapter;
    }

    public static StepUsageDeclarationAdapter getStepUsageDeclarationAdapter() {
        if (stepUsageDeclarationAdapter == null) {
            stepUsageDeclarationAdapter = new StepUsageDeclarationAdapter();
        }
        return stepUsageDeclarationAdapter;
    }
}
