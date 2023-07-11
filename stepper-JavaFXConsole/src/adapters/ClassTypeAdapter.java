package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class ClassTypeAdapter extends TypeAdapter<Class<?>>
{
    public void write(JsonWriter out, Class<?> value) throws IOException
    {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.getName());
    }
    public Class<?> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        try {
            return Class.forName(in.nextString());
        } catch (ClassNotFoundException e) {
            throw new IOException(e);
        }
    }
}
