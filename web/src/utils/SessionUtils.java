package utils;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionUtils
{

        public static String getUsername (HttpServletRequest request) {
            HttpSession session = request.getSession(false);
            Object sessionAttribute = session != null ? session.getAttribute("username") : null;
            return sessionAttribute != null ? sessionAttribute.toString() : null;//todo check if it works after changing user to class
        }

        public static void clearSession (HttpServletRequest request) {
            request.getSession().invalidate();
        }
}
