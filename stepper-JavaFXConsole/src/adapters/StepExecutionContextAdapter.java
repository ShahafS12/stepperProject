package adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.execution.context.StepExecutionContextImpl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

public class StepExecutionContextAdapter extends TypeAdapter<StepExecutionContext> {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(FlowDefinition.class, new FlowDefinitionAdapter())
            .registerTypeAdapter(DataDefinition.class, new DataDefinitionAdapter())
            .create();

    @Override
    public void write(JsonWriter out, StepExecutionContext value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        // Serialize complex StepExecutionContext object as a JSON string
        gson.toJson(value, StepExecutionContextImpl.class, out);
    }

    @Override
    public StepExecutionContext read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        // Deserialize JSON string to a StepExecutionContext object
        return gson.fromJson(in, StepExecutionContextImpl.class);
    }
}
