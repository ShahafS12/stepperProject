package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;
import utils.ServletUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class GetStepExecutionStatisticsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/json");
        FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
        if (flowManager.getFlows().isEmpty()) {
            response.setStatus(400);
        }
        Type type = new TypeToken<Map<String, FlowExecutionStatistics>>() {
        }.getType();
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(flowManager.getStepExecutionStatisticsMap(), type);
        try {
            response.getWriter().println(json);
            response.setStatus(200);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
