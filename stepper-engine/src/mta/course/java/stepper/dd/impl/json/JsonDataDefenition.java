package mta.course.java.stepper.dd.impl.json;

import com.google.gson.JsonElement;
import mta.course.java.stepper.dd.api.AbstractDataDefinition;

public class JsonDataDefenition extends AbstractDataDefinition {
    public JsonDataDefenition() {
        super("Json", true, JsonElement.class);
    }

    public <T> T getValue(String name) {
        return null;
    }
}
