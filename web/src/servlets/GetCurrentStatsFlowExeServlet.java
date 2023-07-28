package servlets;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.dd.api.DataDefinition;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import utils.ServletUtils;

import java.lang.reflect.Type;
import java.util.Map;

public class GetCurrentStatsFlowExeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
        String userName = request.getParameter("userName");
        FlowExecutionStatistics currentStats = flowManager.getCurrentStatsFlowExecuted(userName);
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                .registerTypeAdapter(DataDefinition.class, new DataDefinitionAdapter())
                //.registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                .registerTypeAdapter(StepExecutionContext.class, new StepExecutionContextAdapter())
                .create();
        String json = gson.toJson(currentStats);
        try {
            response.getWriter().println(json);
            response.setStatus(200);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
