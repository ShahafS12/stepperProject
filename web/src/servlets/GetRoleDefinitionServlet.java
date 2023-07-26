package servlets;

import adapters.ClassTypeAdapter;
import adapters.ClassTypeAdapterFactory;
import adapters.DataDefinitionAdapter;
import adapters.DataDefinitionDeclarationAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.manager.FlowManager;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import roles.RoleManager;
import utils.ServletUtils;

import java.io.PrintWriter;

public class GetRoleDefinitionServlet  extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        String roleName = request.getParameter("roleName");
        try(PrintWriter out = response.getWriter()){
            Gson gson = new GsonBuilder()
                    .create();
            RoleManager roleManager = ServletUtils.getRoleManager(getServletContext());
            String json = gson.toJson(roleManager.getRole(roleName));
            out.println(json);
            response.setStatus(HttpServletResponse.SC_OK);
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }

    }
}
