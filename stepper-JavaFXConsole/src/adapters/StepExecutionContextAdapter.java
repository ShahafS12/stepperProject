package adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.context.AutoMapping;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.execution.context.StepExecutionContextImpl;
import mta.course.java.stepper.flow.execution.context.stepAliasing;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

public class StepExecutionContextAdapter extends TypeAdapter<StepExecutionContext> {

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(FlowDefinition.class, new FlowDefinitionAdapter())
            .registerTypeAdapter(DataDefinition.class, new DataDefinitionAdapter())
            .registerTypeAdapter(Class.class, new ClassTypeAdapter())
            .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
            .registerTypeAdapter(new TypeToken<Map<String, Object>>(){}.getType(), new DataValuesMapAdapter())
            .registerTypeAdapter(new TypeToken<Map<AutoMapping, Object>>(){}.getType(), new AutoMappingDeserializer())
            .registerTypeAdapter(new TypeToken<Map<String, String>>(){}.getType(), new StringMapDeserializer())
            .registerTypeAdapter(new TypeToken<Map<String, DataDefinition>>(){}.getType(), new DataDefinitionMapAdapter())
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Override
    public void write(JsonWriter out, StepExecutionContext value) throws IOException {
        out.beginObject();
        out.name("dataValuesMap");
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        String json = gson.toJson(value.getDataValuesMap(), type);
        out.jsonValue(json);
        out.name("dataDefinitions");
        type = new TypeToken<Map<String, DataDefinition>>(){}.getType();
        json = gson.toJson(value.getDataDefinitions(), type);
        out.jsonValue(json);
//        out.name("logs");
//        type = new TypeToken<Map<String, ArrayList<String>>>(){}.getType();
//        json = gson.toJson(value.getLogs(), type);
//        out.jsonValue(json);
//        out.name("summaryLines");
//        type = new TypeToken<Map<String, String>>(){}.getType();
//        json = gson.toJson(value.getSummaryLines(), type);
//        out.jsonValue(json);
//        out.name("flowLevelAliasing");
//        type = new TypeToken<Map<String, String>>(){}.getType();
//        json = gson.toJson(value.getFlowLevelAliasing(), type);
//        out.jsonValue(json);
        out.name("costumeMapping");
        type = new TypeToken<Map<String, String>>(){}.getType();
        json = gson.toJson(value.getCostumeMapping(), type);
        out.jsonValue(json);
        out.name("autoMapping");
        type = new TypeToken<Map<AutoMapping,Object>>(){}.getType();
        json = gson.toJson(value.getAutoMapping(), type);
        out.jsonValue(json);
//        out.name("stepLevelAliasing");
//        type = new TypeToken<Map<String, Queue<stepAliasing>>>(){}.getType();
//        json = gson.toJson(value.getStepLevelAliasing(), type);
//        out.jsonValue(json);
//        out.name("flowDefinition");
//        gson.toJson(value.getFlowDefinition(), FlowDefinition.class, out);
        out.endObject();
    }

    @Override
    public StepExecutionContext read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        StepExecutionContextImpl context = new StepExecutionContextImpl();
        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "dataValuesMap":
                    Type type = new TypeToken<Map<String, Object>>(){}.getType();
                    context.setDataValuesMap(gson.fromJson(in, type));
                    break;
                case "dataDefinitions":
                    type = new TypeToken<Map<String, DataDefinition>>(){}.getType();
                    context.setDataDefinitions(gson.fromJson(in, type));
                    break;
                case "logs":
                    type = new TypeToken<Map<String, ArrayList<String>>>(){}.getType();
                    context.setLogs(gson.fromJson(in, type));
                    break;
                case "summaryLines":
                    type = new TypeToken<Map<String, String>>(){}.getType();
                    context.setSummaryLines(gson.fromJson(in, type));
                    break;
                case "flowLevelAliasing":
                    type = new TypeToken<Map<String, String>>(){}.getType();
                    context.setFlowLevelAliasing(gson.fromJson(in, type));
                    break;
                case "costumeMapping":
                    type = new TypeToken<Map<String, String>>(){}.getType();
                    context.setCostumeMapping(gson.fromJson(in, type));
                    break;
                case "autoMapping":
                    type = new TypeToken<Map<AutoMapping,Object>>(){}.getType();
                    context.setAutoMapping(gson.fromJson(in, type));
                    break;
                case "stepLevelAliasing":
                    type = new TypeToken<Map<String, Queue<stepAliasing>>>(){}.getType();
                    context.setStepLevelAliasing(gson.fromJson(in, type));
                    break;
                case "flowDefinition":
                    context.setFlowDefinition(gson.fromJson(in, FlowDefinition.class));
                    break;
                default:
                    in.skipValue();
                    break;
            }
        }
        in.endObject();
        return context;
    }
}