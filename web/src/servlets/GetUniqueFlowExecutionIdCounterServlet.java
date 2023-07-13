package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.manager.FlowManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class GetUniqueFlowExecutionIdCounterServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter())
        {
            Gson gson = new Gson();
            FlowManager flowManager = ServletUtils.getFlowManager(getServletContext());
            int id = flowManager.getUniqueFlowIdCounter();
            String json = gson.toJson(id);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
