package servlets;

import adapters.SingleStepExecutionAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import utils.ServletUtils;


import java.util.List;

public class GetExecutionDataServlet extends HttpServlet
{
    //gets the latest execution data from the server
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
        List<SingleStepExecutionData> executionData = flowManager.getLatestExecutionData();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(SingleStepExecutionData.class, new SingleStepExecutionAdapter())
                .create();
        String json = gson.toJson(executionData);
        try {
            response.getWriter().println(json);
            response.getWriter().flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
