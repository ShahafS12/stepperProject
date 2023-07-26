package mta.course.java.stepper.dd.impl.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mta.course.java.stepper.dd.api.AbstractDataDefinition;

public class JsonDataDefenition extends AbstractDataDefinition {
    public JsonDataDefenition() {
        super("Json", true, JsonObject.class);
    }

    public <T> T getValue(String name) {
        return null;
    }
}
