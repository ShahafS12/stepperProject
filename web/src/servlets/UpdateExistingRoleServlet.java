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
import java.util.List;

public class UpdateExistingRoleServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            String json = request.getReader().lines().collect(java.util.stream.Collectors.joining());
            Gson gson = new Gson();
            String[] data = gson.fromJson(json, String[].class);
            String roleName = data[0];
            List<String> flowsAllowed = gson.fromJson(data[1], List.class);

            List<String> usersAllowed = gson.fromJson(data[2], List.class);
            RoleManager roleManager = ServletUtils.getRoleManager(getServletContext());
            if(roleName.equals("All Flows")||roleName.equals("Read Only"))
            {//if the role is All Flows or Read Only, we don't want to change the flows allowed
                flowsAllowed = roleManager.getRole(roleName).getFlowsAllowed();
            }
            roleManager.updateRole(roleName, flowsAllowed, usersAllowed);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
