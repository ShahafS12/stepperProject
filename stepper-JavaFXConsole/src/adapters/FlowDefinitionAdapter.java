package adapters;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import org.intellij.lang.annotations.Flow;

public class FlowDefinitionAdapter extends TypeAdapter<FlowDefinition> {
    @Override
    public FlowDefinition read(com.google.gson.stream.JsonReader in) throws java.io.IOException {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                .registerTypeAdapter(DataDefinition.class, new DataDefinitionAdapter())
                .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
                .create();
        return gson.fromJson(in, FlowDefinitionImpl.class);
    }

    @Override
public void write(com.google.gson.stream.JsonWriter out, FlowDefinition value) throws java.io.IOException {
//        Gson gson = new GsonBuilder()
//                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
//                .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
//                .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
//                .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
//                .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
//                .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
//                .create();
//        gson.toJson(value, FlowDefinitionImpl.class, out);
    }

}
