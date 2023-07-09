package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import mta.course.java.stepper.flow.manager.FlowManager;

public class FlowListServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter())
        {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            Set<String> usersList = FlowManager.getFlowNames();
            String json = gson.toJson(usersList);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
