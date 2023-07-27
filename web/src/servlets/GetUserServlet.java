package servlets;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import users.UserManager;
import utils.ServletUtils;

import java.io.PrintWriter;

public class GetUserServlet extends HttpServlet
{
    protected void doGet(HttpServletRequest request, HttpServletResponse response){
        response.setContentType("application/json");
        String userName = request.getParameter("userName");
        try(PrintWriter out = response.getWriter()){
            Gson gson = new GsonBuilder()
                    .create();
            UserManager userManager = ServletUtils.getUserManager(getServletContext());
            String json = gson.toJson(userManager.getUser(userName));
            out.println(json);
            response.setStatus(HttpServletResponse.SC_OK);
            out.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            throw new RuntimeException(e);
        }
    }
}
