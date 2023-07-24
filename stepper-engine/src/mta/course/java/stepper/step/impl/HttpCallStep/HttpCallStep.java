package mta.course.java.stepper.step.impl.HttpCallStep;

import mta.course.java.stepper.dd.impl.DataDefinitionRegistry;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.AbstractStepDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;
import mta.course.java.stepper.step.api.DataNecessity;
import mta.course.java.stepper.step.api.StepResult;
import okhttp3.*;

import java.io.IOException;


public class HttpCallStep extends AbstractStepDefinition {
    public HttpCallStep(){
        super("Http Call", false);
        addInput(new DataDefinitionDeclarationImpl("RESOURCE", DataNecessity.MANDATORY, "Resource Name(include query parameters", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("ADDRESS", DataNecessity.MANDATORY, "Domain:Port", DataDefinitionRegistry.STRING));
        addInput(new DataDefinitionDeclarationImpl("PROTOCOL", DataNecessity.MANDATORY, "protocol", DataDefinitionRegistry.Enumeration));
        addInput(new DataDefinitionDeclarationImpl("METHOD", DataNecessity.OPTIONAL, "Method", DataDefinitionRegistry.Enumeration)); //TODO : get, post, put, delete | default: get
        addInput(new DataDefinitionDeclarationImpl("BODY", DataNecessity.OPTIONAL, "Request body", DataDefinitionRegistry.JSON));

        addOutput(new DataDefinitionDeclarationImpl("CODE", DataNecessity.NA, "Response code", DataDefinitionRegistry.Number));
        addOutput(new DataDefinitionDeclarationImpl("RESPONSE_BODY", DataNecessity.NA, "Response body", DataDefinitionRegistry.STRING));
    }

    @Override
    public StepResult invoke(StepExecutionContext context) {
        String finalStepName = context.getStepAlias(this.name());
        String resource = context.getDataValue(context.getAlias(finalStepName + "." + "RESOURCE", String.class), String.class);
        String address = context.getDataValue(context.getAlias(finalStepName + "." + "ADDRESS", String.class), String.class);
        String protocol = context.getDataValue(context.getAlias(finalStepName + "." + "PROTOCOL", String.class), String.class);
        String method = context.getDataValue(context.getAlias(finalStepName + "." + "METHOD", String.class), String.class);
        String jsonBody = context.getDataValue(context.getAlias(finalStepName + "." + "BODY", String.class), String.class);

        String beforeStarting = "About to invoke http request <request details: " + " | " + protocol + " | " + method + " | " + address + "/" + resource;
        context.addLogLine(finalStepName, beforeStarting);
        Request request;
        OkHttpClient client = new OkHttpClient();


        MediaType MEDIA_TYPE_JSON
                = MediaType.parse("application/json; charset=utf-8");

        String url = protocol + "://" + address + "/" + resource;
        RequestBody body = RequestBody.create(MEDIA_TYPE_JSON, jsonBody);

        switch (method) {
            case "POST":
                request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                break;
            case "PUT":
                request = new Request.Builder()
                        .url(url)
                        .put(body)
                        .build();
                break;
            case "DELETE":
                request = new Request.Builder()
                        .url(url)
                        .delete() // No body for delete request
                        .build();
                break;
            default: // Assuming default is GET
                request = new Request.Builder()
                        .url(url)
                        .get() // No body for get request
                        .build();
                break;
        }

        try {
            Response response = client.newCall(request).execute();
            String afterResponse = "Response received. Status code: <" + response.code() + ">";
            context.storeDataValue(context.getAlias(finalStepName + "." + "CODE", Integer.class), response.code());
            context.storeDataValue(context.getAlias(finalStepName + "." + "RESPONSE_BODY", String.class), response.body().string());
            context.addLogLine(finalStepName, afterResponse);
            context.addSummaryLine(finalStepName, afterResponse);
            return StepResult.SUCCESS;
        } catch (IOException e) {
            String error = "Error while executing request: " + e.getMessage();
            context.addLogLine(finalStepName, error);
            context.addSummaryLine(finalStepName, error);
        }
        return StepResult.FAILURE;
    }


}

