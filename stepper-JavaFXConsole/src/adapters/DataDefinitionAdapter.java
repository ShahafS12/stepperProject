package adapters;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;


import java.io.IOException;
public class DataDefinitionAdapter extends TypeAdapter<DataDefinition> {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
            .registerTypeAdapter(ClassTypeAdapter.class, new ClassTypeAdapter())
            .create();
    @Override
    public void write(JsonWriter out, DataDefinition value) throws IOException {
        out.beginObject();
        out.name("name");
        out.value(value.getName());
        out.name("userFriendly");
        gson.toJson(value.isUserFriendly(), Boolean.class, out);
        out.name("type");
        gson.toJson(value.getType(), Class.class, out);
        out.endObject();
    }

    @Override
    public DataDefinition read(JsonReader in) throws IOException {
        String name = null;
        boolean userFriendly = false;
        Class<?> type = null;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "name":
                    name = in.nextString();
                    break;
                case "userFriendly":
                    userFriendly = in.nextBoolean();
                    break;
                case "type":
                    try {
                        type = Class.forName(in.nextString()); // read the class name as string
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
        in.endObject();

        // Use the name to determine which specific DataDefinition to return
        for (DataDefinitionRegistry registry : DataDefinitionRegistry.values()) {
            if (registry.getName().equals(name)) {
                return registry; // return the corresponding DataDefinition from the enum
            }
        }

        // If no matching DataDefinition is found, you might want to throw an exception
        throw new IllegalArgumentException("No matching DataDefinition found for name: " + name);
    }
}
