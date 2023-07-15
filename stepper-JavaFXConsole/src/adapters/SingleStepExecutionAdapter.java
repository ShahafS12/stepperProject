package adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SingleStepExecutionAdapter extends TypeAdapter<SingleStepExecutionData>
{

    @Override
    public void write(JsonWriter out, SingleStepExecutionData data) throws IOException
    {
        out.beginObject();
        out.name("duration").value(data.getDuration());
        out.name("success").value(data.getSuccess().toString()); // Assuming StepResult has a reasonable toString method
        out.name("summaryLine").value(data.getSummaryLine());
        out.name("stepName").value(data.getStepName());

        out.name("logs").beginArray();
        for (String log : data.getLogs()) {
            out.value(log);
        }
        out.endArray();

        out.endObject();
    }

    @Override
    public SingleStepExecutionData read(JsonReader in) throws IOException {
        double duration = 0.0;
        StepResult success = null;
        String summaryLine = null;
        String stepName = null;
        List<String> logs = new ArrayList<>();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "duration":
                    duration = in.nextDouble();
                    break;
                case "success":
                    success = StepResult.valueOf(in.nextString()); // Assuming StepResult is an enum
                    break;
                case "summaryLine":
                    summaryLine = in.nextString();
                    break;
                case "stepName":
                    stepName = in.nextString();
                    break;
                case "logs":
                    in.beginArray();
                    while (in.hasNext()) {
                        logs.add(in.nextString());
                    }
                    in.endArray();
                    break;
            }
        }
        in.endObject();

        return new SingleStepExecutionData(duration, success, summaryLine, logs, stepName);
    }
}
