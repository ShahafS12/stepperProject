package servlets;

import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExampleJsonServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", "John Doe");
        jsonObject.addProperty("age", "30");

        JsonObject addressObject = new JsonObject();
        addressObject.addProperty("city", "New York");
        addressObject.addProperty("zip code", "10001");
        jsonObject.add("address", addressObject);

        response.setContentType("application/json");
        try {
            response.getWriter().println(jsonObject);
            response.setStatus(200);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
