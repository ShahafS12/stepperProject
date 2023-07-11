package adapters;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;

import java.io.IOException;

public class DataDefinitionDeclarationAdapter extends TypeAdapter<DataDefinitionDeclaration> {
    private final Gson gson = new Gson();

    @Override
    public void write(JsonWriter out, DataDefinitionDeclaration value) throws IOException {
        out.beginObject();
        out.name("name");
        out.value(value.getName());

        out.name("necessity");
        if (value.necessity() != null) {
            gson.toJson(value.necessity(), DataNecessity.class, out);
        } else {
            out.value(value.necessity().toString());
        }

        out.name("userString");
        out.value(value.userString());

        out.name("dataDefinition");
        if (value.dataDefinition() != null) {
            gson.toJson(value.dataDefinition(), DataDefinition.class, out);
        } else {
            out.value(value.dataDefinition().toString());
        }

        out.endObject();
    }

    @Override
    public DataDefinitionDeclaration read(JsonReader in) throws IOException {
        in.beginObject();
        String name = null;
        String necessityString = null;
        DataNecessity dataNecessity = null;
        String userString = null;
        DataDefinition dataDefinition = null;
        String dataDefinitionString = null;
        while (in.hasNext()) {
            String nextName = in.nextName();
            switch (nextName) {
                case "name":
                    name = in.nextString();
                    break;
                case "necessity":
                    try {
                        dataNecessity = gson.fromJson(in, DataNecessity.class);
                    } catch (JsonParseException e) {
                        necessityString = in.nextString();
                    }
                    break;
                case "userString":
                    userString = in.nextString();
                    break;
                case "dataDefinition":
                    try {
                        dataDefinition = gson.fromJson(in, DataDefinition.class);
                    } catch (JsonParseException e) {
                        dataDefinitionString = in.nextString();
                    }
                    break;
            }
        }
        in.endObject();
        // assuming you have a constructor that matches these parameters
        return new DataDefinitionDeclarationImpl(name, dataNecessity, userString, dataDefinition);
    }







}
