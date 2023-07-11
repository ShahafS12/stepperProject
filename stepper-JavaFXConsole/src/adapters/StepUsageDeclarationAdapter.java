package adapters;

import adapters.gsonFactory.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;

import java.io.IOException;

public class StepUsageDeclarationAdapter extends TypeAdapter<StepUsageDeclaration>
{
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(StepDefinition.class, GsonFactory.getStepDefinitionAdapter())
            .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
            .create();

    @Override
    public void write(JsonWriter out, StepUsageDeclaration value) throws IOException
    {
        out.beginObject();
        out.name("stepDefinition");
        gson.toJson(value.getStepDefinition(), StepDefinition.class, out);
        out.name("skipIfFail");
        out.value(value.skipIfFail());
        out.name("stepName");
        out.value(value.getStepName());
        out.name("stepAlias");
        out.value(value.getFinalStepName());
        out.endObject();
    }

    @Override
    public StepUsageDeclaration read(JsonReader in) throws IOException {
        StepDefinition stepDefinition = null;
        boolean skipIfFail = false;
        String stepName = null;
        String stepAlias = null;

        in.beginObject();
        while (in.hasNext()) {
            String nextName = in.nextName();
            switch (nextName) {
                case "stepDefinition":
                    stepDefinition = gson.fromJson(in, StepDefinition.class);
                    break;
                case "skipIfFail":
                    skipIfFail = in.nextBoolean();
                    break;
                case "stepName":
                    stepName = in.nextString();
                    break;
                case "stepAlias":
                    stepAlias = in.nextString();
                    break;
            }
        }
        in.endObject();

        return new StepUsageDeclarationImpl(stepDefinition, skipIfFail, stepName, stepAlias);
    }
}
