package servlets;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import utils.ServletUtils;

import java.io.PrintWriter;

public class getFlowDefinitionServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        String flowName = request.getParameter("flowName");
        try(PrintWriter out = response.getWriter()){
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                    .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                    .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                    //.registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                    .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                    .create();
            FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
            FlowDefinition flowDefinition = flowManager.getFlowDefinition(flowName);
            String json = gson.toJson(flowDefinition);
            out.println(json);
            out.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
