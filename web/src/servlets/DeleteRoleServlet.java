package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roles.RoleDefinitionImpl;
import roles.RoleManager;
import users.UserDefinition;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class DeleteRoleServlet extends HttpServlet
{
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
    {
        try {
            String json = request.getReader().lines().collect(java.util.stream.Collectors.joining());
            Gson gson = new Gson();
            String roleName = request.getParameter("roleName");
            RoleManager roleManager = ServletUtils.getRoleManager(getServletContext());
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            roleManager.removeRole(roleName);
            Set<UserDefinition> users = userManager.getUsers();
            for(UserDefinition user : users) {
                Set<RoleDefinitionImpl> roles = user.getRoles();
                for (RoleDefinitionImpl role : roles) {
                    if (role.getRoleName().equals(roleName)) {
                        user.removeRole(roleName);
                    }
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
