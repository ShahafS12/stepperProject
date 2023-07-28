package servlets;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclarationImpl;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepDefinition;
import utils.ServletUtils;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

public class ExecuteFlowServlet extends HttpServlet
{
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException
    {
        String json = request.getReader().lines().collect(Collectors.joining());
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
                .create();
        try{
            String[] data = gson.fromJson(json, String[].class);
            String flowName = data[0];
            List<Object> mandatoryInputsList = gson.fromJson(data[1], List.class);
            List<Object> optionalInputsList = gson.fromJson(data[2], List.class);
            Type outputListType = new TypeToken<List<InputWithStepName>>(){}.getType();
            List<InputWithStepName> outputsList = gson.fromJson(data[3], outputListType);

            Type executionDataType = new TypeToken<List<SingleStepExecutionData>>(){}.getType();
            List<SingleStepExecutionData> executionDataList = gson.fromJson(data[4], executionDataType);
            String userName = gson.fromJson(data[5], String.class);
            FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
            FlowDefinition chosenFlow = flowManager.getFlowDefinition(flowName);
            int uniqueFlowId = flowManager.getUniqueFlowIdCounter();//todo this might need to be synchronized
            for(StepUsageDeclaration stepUsageDeclaration : chosenFlow.getFlowSteps()){
                if(stepUsageDeclaration.getStepName().equals("Spend Some Time")
                ){//fixing it comes as double from the client
                    for(int i = 0;i<mandatoryInputsList.size();i++){
                        if(mandatoryInputsList.get(i) instanceof Double){
                            mandatoryInputsList.set(i, ((Number) mandatoryInputsList.get(i)).intValue());
                        }
                    }
                }
            }
            try {
                CountDownLatch latch = new CountDownLatch(1);
                flowManager.executeFlow(chosenFlow, mandatoryInputsList, optionalInputsList, outputsList, executionDataList, latch, userName);
                latch.await();  // This will block until the latch count reaches zero
                response.setStatus(HttpServletResponse.SC_OK);
            }
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().println(e.getMessage());
            }
            catch (Exception e){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println(e.getMessage());
            }
    }
        catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(e.getMessage());
        }//todo add to web xml
    }
}

