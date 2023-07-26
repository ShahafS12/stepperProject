package servlets;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

public class GetUsersListServlet extends HttpServlet
{
    //gets the list of users from the server
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    {
        response.setContentType("application/json");
        try (PrintWriter out = response.getWriter())
        {
            Gson gson = new Gson();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            List<String> userList = userManager.getUsersNames();
            String json = gson.toJson(userList);
            out.println(json);
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
