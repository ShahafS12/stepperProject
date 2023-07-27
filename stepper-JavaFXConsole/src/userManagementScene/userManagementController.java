package userManagementScene;

import api.HttpStatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
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
import roles.RoleDefinitionImpl;
import users.UserImpl;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.GET_ROLE_DEFINITION;
import static util.Constants.GET_USER;

public class userManagementController {

    @FXML
    private AnchorPane userManagementAnchorPane;

    @FXML
    private ListView<String> availableUsersListView;
    @FXML
    private VBox chosenRoleInfo;

    @FXML
    private Button save;
    private mainAdminController mainController;
    private Timer timer;
    private String lastSelectedUser;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private TimerTask listRefresher;
    private HttpStatusUpdate httpStatusUpdate;
    private Set<String> selectedRoles = new HashSet<>();
    private boolean selectedManager;
    private boolean firstTime = true;

    @FXML
    void saveUserInfo(ActionEvent event) {

    }
    public void initialize(){
        if(availableUsersListView != null) {
            availableUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null&& !newValue.equals(oldValue)) {
                    handleUserSelection(newValue);
                }
            });
        }
    }

    private void handleUserSelection(String newValue)
    {
        lastSelectedUser = newValue;
        String finalUrl = HttpUrl.parse(GET_USER)
                .newBuilder()
                .addQueryParameter("roleName", newValue)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
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
                            UserImpl user = gson.fromJson(roleDefinitionFromServer, UserImpl.class);
                            setChosenUserData(user);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        });
    }
    public void setChosenUserData(UserImpl user)
    {

    }
}