package adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataValuesMapAdapter implements JsonDeserializer<Map<String, Object>>
{

    @Override
    public Map<String, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, Object> dataValuesMap = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            JsonElement value = entry.getValue();
            if (value.isJsonPrimitive()) {
                JsonPrimitive primitive = value.getAsJsonPrimitive();
                if (primitive.isBoolean()) {
                    dataValuesMap.put(entry.getKey(), primitive.getAsBoolean());
                } else if (primitive.isString()) {
                    dataValuesMap.put(entry.getKey(), primitive.getAsString());
                } else if (primitive.isNumber()) {
                    dataValuesMap.put(entry.getKey(), primitive.getAsNumber());
                }
            } else if (value.isJsonArray()) {
                dataValuesMap.put(entry.getKey(), context.deserialize(value, new TypeToken<ArrayList<Object>>(){}.getType()));
            } else if (value.isJsonObject()) {
                dataValuesMap.put(entry.getKey(), context.deserialize(value, new TypeToken<Map<String, Object>>(){}.getType()));
            } else {
                dataValuesMap.put(entry.getKey(), null);
            }
        }

        return dataValuesMap;
    }
}





