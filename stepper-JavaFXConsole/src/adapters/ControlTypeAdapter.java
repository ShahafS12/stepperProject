package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import javafx.scene.control.Control;

import java.io.IOException;

public class ControlTypeAdapter extends TypeAdapter<Control>
{
    @Override
    public void write(JsonWriter out, Control value) throws IOException
    {
        out.beginObject();
        out.name("id").value(value.getId());
        out.name("layoutX").value(value.getLayoutX());
        out.name("layoutY").value(value.getLayoutY());
        // Write more properties as needed
        out.endObject();
    }

    @Override
    public Control read(JsonReader in) throws IOException {
        // This method might not be useful in your case because, as mentioned before,
        // you can't fully recreate a Control object just from the serialized data.
        // So you may leave this method throwing an UnsupportedOperationException
        throw new UnsupportedOperationException("Deserializing Control is not supported.");
    }
}
