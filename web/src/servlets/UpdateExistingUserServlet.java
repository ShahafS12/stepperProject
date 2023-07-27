package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roles.RoleManager;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;
import java.util.List;

public class UpdateExistingUserServlet extends HttpServlet
{
    protected void doPost(HttpServletRequest request, HttpServletResponse response){
        try {
            String json = request.getReader().lines().collect(java.util.stream.Collectors.joining());
            Gson gson = new Gson();
            String[] data = gson.fromJson(json, String[].class);
            String userName = data[0];
            List<String> selectedRoles = gson.fromJson(data[1], List.class);
            boolean isManager = gson.fromJson(data[2], boolean.class);
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            userManager.updateUser(userName, selectedRoles, isManager);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
