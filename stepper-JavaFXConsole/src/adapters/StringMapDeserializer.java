package adapters;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import mta.course.java.stepper.flow.execution.context.AutoMapping;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StringMapDeserializer implements JsonDeserializer<Map<String, String>> {
    @Override
    public Map<String, String> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Map<String, String> map = new HashMap<>();
        JsonObject jsonObject = json.getAsJsonObject();

        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String key = entry.getKey();
            JsonElement valueElement = entry.getValue();

            // Ensure the JSON element is a primitive and can be interpreted as a string.
            if (valueElement.isJsonPrimitive()) {
                String value = valueElement.getAsString();
                map.put(key, value);
            } else {
                throw new JsonParseException("Invalid type for map value, expected string: " + valueElement);
            }
        }

        return map;
    }
}
