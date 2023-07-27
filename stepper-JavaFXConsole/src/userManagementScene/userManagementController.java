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
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import mainSceneAdmin.mainAdminController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import roles.RoleDefinitionImpl;
import users.UserImpl;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;

import static util.Constants.*;

public class userManagementController {

    @FXML
    private AnchorPane userManagementAnchorPane;

    @FXML
    private ListView<String> availableUsersListView;
    @FXML
    private VBox chosenUserInfo;

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
    private UserImpl user;

    @FXML
    void saveUserInfo(ActionEvent event) {
        Gson gson = new Gson();
        if(this.user!=null){
            String[] jsonArray = {user.getUsername(),gson.toJson(selectedRoles),gson.toJson(selectedManager)};
            String json = gson.toJson(jsonArray);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(UPDATE_USER)
                    .post(body)
                    .build();
            Response response = HttpClientUtil.run(request);
        }
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
                .addQueryParameter("userName", newValue)
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
        if(lastSelectedUser!=null && !user.getUsername().equals(lastSelectedUser)) {
            return;
        }
        this.user = user;
        if(user == null){
            return;
        }
        if(chosenUserInfo != null) {
            chosenUserInfo.getChildren().clear();
        }
        Text username = new Text("Username: ");
        username.setStyle("-fx-font-weight: bold");
        chosenUserInfo.getChildren().add(username);
        chosenUserInfo.getChildren().add(new Text(user.getUsername()));
        Text roles = new Text("Roles: ");
        roles.setStyle("-fx-font-weight: bold");
        chosenUserInfo.getChildren().add(roles);
        String rolesToAssignUrl = HttpUrl.parse(ROLES_LIST)
                .newBuilder()
                .build()
                .toString();
        Response response = HttpClientUtil.run(rolesToAssignUrl);
        Gson gson = new GsonBuilder()
                .create();
        List<String> rolesList = gson.fromJson(response.body().charStream(), List.class);
        for(String role : rolesList){
            RadioButton radioButton = new RadioButton(role);
            if((user.getRoles().contains(role)&&firstTime) || selectedRoles.contains(role))
            {
                //mark the radio button as selected
                radioButton.setSelected(true);
                selectedRoles.add(role);
            }
            else {
                radioButton.setSelected(false);
            }
            radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedRoles.add(role);
                } else {
                    selectedRoles.remove(role);
                }
            });
            if(user.isUserAdmin())
            {
                chosenUserInfo.getChildren().add(new Text(role));
            }
            else
                chosenUserInfo.getChildren().add(radioButton);
            if(user.getRoles().contains(role))
            {
                Text assigned = new Text("ASSIGNED");
                assigned.setStyle("-fx-font-weight: bold; -fx-fill: green");
                chosenUserInfo.getChildren().add(assigned);
                chosenUserInfo.getChildren().add(new Text("\n"));
            }
            else {
                Text notAssigned = new Text("NOT ASSIGNED");
                notAssigned.setStyle("-fx-font-weight: bold; -fx-fill: red");
                chosenUserInfo.getChildren().add(notAssigned);
                chosenUserInfo.getChildren().add(new Text("\n"));
            }
        }
        Text manager = new Text("Manager: ");
        manager.setStyle("-fx-font-weight: bold");
        chosenUserInfo.getChildren().add(manager);
        RadioButton managerRadioButton = new RadioButton("Manager");
        if((user.isManager()&&firstTime) || selectedManager)
        {
            //mark the radio button as selected
            managerRadioButton.setSelected(true);
            selectedManager = true;
        }
        else {
            managerRadioButton.setSelected(false);
        }
        managerRadioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                selectedManager = true;
            } else {
                selectedManager = false;
            }
        });
        chosenUserInfo.getChildren().add(managerRadioButton);
        if(user.isManager())
        {
            Text assigned = new Text("ASSIGNED");
            assigned.setStyle("-fx-font-weight: bold; -fx-fill: green");
            chosenUserInfo.getChildren().add(assigned);
            chosenUserInfo.getChildren().add(new Text("\n"));
        }
        else {
            Text notAssigned = new Text("NOT ASSIGNED");
            notAssigned.setStyle("-fx-font-weight: bold; -fx-fill: red");
            chosenUserInfo.getChildren().add(notAssigned);
            chosenUserInfo.getChildren().add(new Text("\n"));
        }
        firstTime = false;
    }
    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate)
    {
        this.httpStatusUpdate = httpStatusUpdate;
    }
    public void setMainController(mainAdminController mainAdminController)
    {
        this.mainController = mainAdminController;
    }
    public AnchorPane getUserManagementAnchorPane()
    {
        return userManagementAnchorPane;
    }
    public void setUserList(List<String> userList)
    {
        availableUsersListView.getItems().clear();
        availableUsersListView.getItems().addAll(userList);
        availableUsersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null&& !newValue.equals(oldValue)) {
                handleUserSelection(newValue);
            }
        });
        if(lastSelectedUser!=null) {
            availableUsersListView.getSelectionModel().select(lastSelectedUser);
        }
        else {
            availableUsersListView.getSelectionModel().select(lastSelectedUser);
        }
    }

    public void startListRefresher(){
        listRefresher = new UserListRefresher(
                httpStatusUpdate::updateHttpLine,
                this::setUserList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

}