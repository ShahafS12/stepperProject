package rolesManagementScene;

import api.HttpStatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import mainSceneAdmin.mainAdminController;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import roles.RoleDefinition;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.GET_ROLE_DEFINITION;
import static util.Constants.REFRESH_RATE;

public class rolesManagementController
{
    @FXML
    private AnchorPane rolesManagementAnchorPane;

    @FXML
    private Button newRoleButton;

    @FXML
    private ListView<String> availableRolesListView;

    @FXML
    private VBox chosenRoleInfo;

    @FXML
    private Button SaveButton;
    private mainAdminController mainController;
    private Timer timer;
    private String lastSelectedRole;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private final BooleanProperty autoUpdate;
    private TimerTask listRefresher;
    private HttpStatusUpdate httpStatusUpdate;

    public void initialize(){
        if(availableRolesListView != null) {
            availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleRoleSelection(newValue);
                }
            });
        }
    }
    public rolesManagementController()
    {
        autoUpdate = new SimpleBooleanProperty();;
    }

    @FXML
    public void SaveRoles(ActionEvent event) {
        return;
    }

    @FXML
    public void createNewRole(ActionEvent event) {
        return;
    }

    public void setMainController(mainAdminController mainAdminController)
    {
        this.mainController = mainAdminController;
    }

    public AnchorPane getRolesManagementAnchorPane()
    {
        return rolesManagementAnchorPane;
    }
    public void setRolesList(List<String> rolesList)
    {
        availableRolesListView.getItems().clear();
        availableRolesListView.getItems().addAll(rolesList);
        availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                handleRoleSelection(newValue);
            }
        });
        if(lastSelectedRole != null) {
            availableRolesListView.getSelectionModel().select(lastSelectedRole);
        }
    }

    private void handleRoleSelection(String newValue)
    {
        lastSelectedRole = newValue;
        String finalUrl = HttpUrl.parse(GET_ROLE_DEFINITION)
                .newBuilder()
                .addQueryParameter("roleName", newValue)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                System.out.println("Error getting role definition");//todo should it be a log?
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)//todo check how to print the error message to screen
                    );
                }
                else {
                    Platform.runLater(() ->
                    {
                        try {
                            String roleDefinitionFromServer = response.body().string();
                            Gson gson = new GsonBuilder()
                                    .create();
                            RoleDefinition role = gson.fromJson(roleDefinitionFromServer, RoleDefinition.class);
                            setChosenRoleData(role);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }
    public void setChosenRoleData(RoleDefinition role)
    {
        //todo implement
    }
    public void startListRefresher(){
        listRefresher = new RolesListRefresher(
                autoUpdate,
                httpStatusUpdate::updateHttpLine,
                this::setRolesList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);

    }

    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate)
    {
        this.httpStatusUpdate = httpStatusUpdate;
    }
}
