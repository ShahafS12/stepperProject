package adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.execution.context.AutoMapping;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataDefinitionMapAdapter extends TypeAdapter<Map<String, DataDefinition>> {
    private final DataDefinitionAdapter dataDefinitionAdapter = new DataDefinitionAdapter();

    @Override
    public void write(JsonWriter out, Map<String, DataDefinition> map) throws IOException
    {
        out.beginObject();
        for (Map.Entry<String, DataDefinition> entry : map.entrySet()) {
            out.name(entry.getKey());
            dataDefinitionAdapter.write(out, entry.getValue());
        }
        out.endObject();
    }

    @Override
    public Map<String, DataDefinition> read(JsonReader in) throws IOException {
        Map<String, DataDefinition> map = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            DataDefinition value = dataDefinitionAdapter.read(in);
            map.put(key, value);
        }
        in.endObject();

        return map;
    }
}
