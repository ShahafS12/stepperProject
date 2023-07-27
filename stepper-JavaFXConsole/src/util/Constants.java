package util;

import com.google.gson.Gson;

public class Constants
{
    // global constants
    public final static String LINE_SEPARATOR = System.getProperty("line.separator");
    public final static String JHON_DOE = "<Anonymous>";
    public final static int REFRESH_RATE = 2000;
    public final static String CHAT_LINE_FORMATTING = "%tH:%tM:%tS | %.10s: %s%n";

    // fxml locations
    //TODO: change this to the correct location
    //public final static String MAIN_PAGE_FXML_RESOURCE_LOCATION = "/chat/client/component/main/chat-app-main.fxml";
    public final static String MAIN_PAGE_CLIENT_FXML_RESOURCE_LOCATION = "/mainSceneClient/mainSceneClient.fxml";
    public final static String HISTORY_PAGE_FXML_RESOURCE_LOCATION = "/historyScene/historySceneBuilder.fxml";
    public final static String EXECUTION_PAGE_FXML_RESOURCE_LOCATION = "/executionScene/executionScene.fxml";
    public final static String ROLES_MANAGEMENT_PAGE_FXML_RESOURCE_LOCATION = "/rolesManagementScene/rolesManagementScene.fxml";
    public final static String USERS_MANAGEMENT_PAGE_FXML_RESOURCE_LOCATION = "/userManagementScene/userManagementUpdated.fxml";

    // Server resources locations

    public final static String BASE_DOMAIN = "localhost";
    private final static String BASE_URL = "http://" + BASE_DOMAIN + ":8080";
    private final static String CONTEXT_PATH = "/web_Web_exploded";
    private final static String FULL_SERVER_PATH = BASE_URL + CONTEXT_PATH;

    public final static String LOGIN_PAGE = FULL_SERVER_PATH + "/login";
    public final static String ADD_XML_PAGE = FULL_SERVER_PATH + "/addXML";
    public final static String FLOW_LIST = FULL_SERVER_PATH + "/flowList";
    public final static String ROLES_LIST = FULL_SERVER_PATH + "/rolesList";
    public final static String USER_LIST = FULL_SERVER_PATH + "/userList";
    public final static String LOGOUT = FULL_SERVER_PATH + "/logout";
    public final static String EXECUTE_FLOW = FULL_SERVER_PATH + "/execute";
    public final static String GET_FLOW_DEFINITION = FULL_SERVER_PATH + "/getFlowDefinition";
    public final static String GET_ROLE_DEFINITION = FULL_SERVER_PATH + "/getRoleDefinition";
    public final static String GET_FLOW_ID_UNIQUE_EXECUTION_ID = FULL_SERVER_PATH + "/UniqueFlowExecutionIdCounter";
    public final static String GET_EXECUTION_DATA = FULL_SERVER_PATH + "/executionData";
    public final static String GET_FLOWS_ADMIN_HISTORY = FULL_SERVER_PATH + "/getFlowsAdminHistory";
    public final static String GET_FLOW_EXECUTIONS_STATISTICS = FULL_SERVER_PATH + "/getExecutionsStatistics";
    public final static String GET_STEP_EXECUTION_STATISTICS = FULL_SERVER_PATH + "/getStepExecutionStatistics";
    public final static String CREATE_ROLE = FULL_SERVER_PATH + "/createRole";
    public final static String UPDATE_ROLE = FULL_SERVER_PATH + "/updateRole";
    public final static String  UPDATE_USER = FULL_SERVER_PATH + "/updateUser";
    public final static String GET_USER = FULL_SERVER_PATH + "/getUser";
    public final static String EXAMPLE_JSON = FULL_SERVER_PATH + "/exampleJson";


    // GSON instance
    public final static Gson GSON_INSTANCE = new Gson();
}
