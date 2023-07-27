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
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            if(roleName.equals("All Flows")||roleName.equals("Read Only"))
            {//if the role is All Flows or Read Only, we don't want to change the flows allowed
                flowsAllowed = roleManager.getRole(roleName).getFlowsAllowed();
            }
            roleManager.updateRole(roleName, flowsAllowed, usersAllowed);
            Set<UserDefinition> users = userManager.getUsers();
            for(UserDefinition user : users) {
                Set<RoleDefinitionImpl> roles = user.getRoles();
                for (RoleDefinitionImpl role : roles) {
                    if (role.getRoleName().equals(roleName) && !usersAllowed.contains(user.getUsername())) {
                        user.removeRole(roleName);
                    }
                }
                boolean flag = true;
                // none of the roles of the user is the role we want to add
                for (RoleDefinitionImpl role : roles) {
                    if (role.getRoleName().equals(roleName)) {
                        flag = false;
                    }
                }
                if (flag && usersAllowed.contains(user.getUsername())) {
                    RoleDefinitionImpl newRole = new RoleDefinitionImpl(roleName, roleManager.getRole(roleName).getRoleDescription(), flowsAllowed, usersAllowed);
                    user.addRole(newRole);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
