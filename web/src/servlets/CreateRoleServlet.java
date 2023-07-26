package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roles.RoleManager;
import users.UserManager;
import utils.ServletUtils;

import java.util.List;

public class CreateRoleServlet extends HttpServlet
{
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException
    {
        String json = request.getReader().lines().collect(java.util.stream.Collectors.joining());
        Gson gson = new Gson();
        String[] data = gson.fromJson(json, String[].class);
        String roleName = data[0];
        String roleDescription = data[1];
        List<String> flowsAllowed = gson.fromJson(data[2], List.class);
        List<String> usersAllowed = gson.fromJson(data[3], List.class);
        if(roleName.equals("")|| roleDescription.equals("")){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Role name and description must be filled");
            return;
        }
        RoleManager roleManager = ServletUtils.getRoleManager(getServletContext());
        roleManager.addRole(roleName, roleDescription, flowsAllowed, usersAllowed);
        UserManager userManager = ServletUtils.getUserManager(getServletContext());
        userManager.addRoleToUsers(usersAllowed, roleName);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
