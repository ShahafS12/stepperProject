package utils;


import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import mta.course.java.stepper.flow.manager.FlowManager;
import users.UserManager;


public class ServletUtils {

    private static final String USER_MANAGER_ATTRIBUTE_NAME = "userManager";
    private static final String FLOW_MANAGER_ATTRIBUTE_NAME = "flowManager";

    /*
    Note how the synchronization is done only on the question and\or creation of the relevant managers and once they exists -
    the actual fetch of them is remained un-synchronized for performance POV
     */
    private static final Object userManagerLock = new Object();
    private static final Object chatManagerLock = new Object();

    public static UserManager getUserManager(ServletContext servletContext) {

        synchronized (userManagerLock) {
            if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME) == null) {
                UserManager userManager = new UserManager();
                servletContext.setAttribute(USER_MANAGER_ATTRIBUTE_NAME, userManager);
            }
        }
        return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE_NAME);
    }
    public static FlowManager getFlowManager(ServletContext servletContext) {

        synchronized (chatManagerLock) {
            if (servletContext.getAttribute(FLOW_MANAGER_ATTRIBUTE_NAME) == null) {
                FlowManager flowManager = new FlowManager();
                servletContext.setAttribute(FLOW_MANAGER_ATTRIBUTE_NAME, flowManager);
            }
        }
        return (FlowManager) servletContext.getAttribute(FLOW_MANAGER_ATTRIBUTE_NAME);
    }

    public static int getIntParameter(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException numberFormatException) {
            }
        }
        return -1;
    }
}