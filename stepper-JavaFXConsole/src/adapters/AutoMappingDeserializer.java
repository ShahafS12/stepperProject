package adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import mta.course.java.stepper.flow.execution.context.AutoMapping;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AutoMappingDeserializer implements JsonDeserializer<Map<AutoMapping, Object>> {
    @Override
    public Map<AutoMapping, Object> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<AutoMapping, Object> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("@")[0].split("-");
            try {
                if (parts.length > 1) {
                    Class<?> type = Class.forName(parts[0]);
                    String name = parts[1];
                    AutoMapping autoMapping = new AutoMapping(type, name);

                    JsonElement valueElement = entry.getValue();
                    Object value;
                    if (valueElement.isJsonPrimitive()) {
                        JsonPrimitive primitive = valueElement.getAsJsonPrimitive();
                        if (primitive.isBoolean()) {
                            value = primitive.getAsBoolean();
                        } else if (primitive.isString()) {
                            value = primitive.getAsString();
                        } else {
                            value = primitive.getAsNumber();
                        }
                    } else if (valueElement.isJsonArray()) {
                        value = context.deserialize(valueElement, new TypeToken<ArrayList<Object>>(){}.getType());
                    } else {
                        value = context.deserialize(valueElement, new TypeToken<Map<String, Object>>(){}.getType());
                    }
                    map.put(autoMapping, value);
                } else {
                    System.out.println("Warning: Key " + key + " does not contain a '-' character.");
                }
            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
        }

        return map;
    }
}
