package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roles.RoleManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;

public class GetRolesListServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter()) {
            Gson gson = new GsonBuilder()
                    .create();
            RoleManager roleManager = ServletUtils.getRoleManager(getServletContext());
            String json = gson.toJson(roleManager.getRolesNames());
            out.println(json);
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
