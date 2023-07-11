package adapters;

import adapters.gsonFactory.GsonFactory;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.step.StepDefinitionRegistry;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

public class StepDefinitionAdapter extends TypeAdapter<StepDefinition>
{

    //private final TypeAdapter<JsonElement> elementAdapter = GsonFactory.getDefaultInstance().getAdapter(JsonElement.class);


    @Override
    public void write(JsonWriter out, StepDefinition value) throws IOException
    {
        // Omitted for brevity
    }

    @Override
    public StepDefinition read(JsonReader in) throws IOException
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                .create();
        // Make a new JsonParser
        JsonParser parser = new JsonParser();
        // Read the current stepDefinition as a JsonObject
        JsonObject obj = parser.parse(in).getAsJsonObject();

        // Determine the type of the stepDefinition from the "stepName" field
        String type = obj.get("stepName").getAsString();

        // Map type to corresponding class
        Class<? extends StepDefinition> stepClass = determineStepClass(type);

        // Now we can use Gson to deserialize the object
        return gson.getAdapter(stepClass).read(new JsonReader(new StringReader(obj.toString())));
    }

    private Class<? extends StepDefinition> determineStepClass(String type)
    {
        // Use the fromString method from your StepDefinitionRegistry enum
        StepDefinitionRegistry stepDefRegistry = StepDefinitionRegistry.fromString(type);

        // Check if a matching type was found in the registry
        if (stepDefRegistry != null) {
            // Get the class of the stepDefinition from the registry
            return stepDefRegistry.getStepDefinition().getClass();
        } else {
            // If no matching type was found, throw an exception or handle it as appropriate
            throw new IllegalArgumentException("Unrecognized type: " + type);
        }
    }
}
