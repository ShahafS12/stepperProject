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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mainSceneAdmin.mainAdminController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import roles.RoleDefinition;
import roles.RoleDefinitionImpl;
import util.http.HttpClientUtil;

import javafx.scene.text.Text;
import java.io.IOException;
import java.util.*;

import static util.Constants.*;

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
    private Set<String> selectedUsers = new HashSet<>();
    private Set<String> selectedFlows = new HashSet<>();
    private RoleDefinition roleDefinition;
    private boolean firstTime = true;

    public void initialize(){
        if(availableRolesListView != null) {
            availableRolesListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null&& !newValue.equals(oldValue)) {
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
        Gson gson = new Gson();
        if(this.roleDefinition!=null)
        {
            String[] jsonArray = {roleDefinition.getRoleName(),gson.toJson(selectedFlows),gson.toJson(selectedUsers)};
            String json = gson.toJson(jsonArray);
            RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
            Request request = new Request.Builder()
                    .url(UPDATE_ROLE)
                    .post(body)
                    .build();
            Response response = HttpClientUtil.run(request);
            if(response.code() == 200)
            {
                errorMessageProperty.setValue("Role updated successfully");
                errorMessageProperty.setValue("");
            }
            else
            {
                errorMessageProperty.setValue("Error updating role");
            }
            this.firstTime = true;//to refresh the list properly
        }

    }

    @FXML
    public void createNewRole(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/addRoleScene/addRoles.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));
            stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
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
            if(newValue != null&& !newValue.equals(oldValue)) {
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
                            RoleDefinitionImpl role = gson.fromJson(roleDefinitionFromServer, RoleDefinitionImpl.class);
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
        if(this.roleDefinition!=null && this.roleDefinition.getRoleName()!=role.getRoleName())
        {
            firstTime = true;
        }
        this.roleDefinition =  role;
        if(role == null)
        {
            return;
        }
        if(chosenRoleInfo!=null) {
            chosenRoleInfo.getChildren().clear();
        }
        Text roleName = new Text("Role name: ");
        roleName.setStyle("-fx-font-weight: bold");
        chosenRoleInfo.getChildren().add(roleName);
        chosenRoleInfo.getChildren().add(new Text(role.getRoleName()));
        Text description  = new Text("Description: ");
        description.setStyle("-fx-font-weight: bold");
        chosenRoleInfo.getChildren().add(description);
        chosenRoleInfo.getChildren().add(new Text(role.getRoleDescription() + "\n"));
        Text availableRolesToAssign = new Text("Available roles to assign: ");
        availableRolesToAssign.setStyle("-fx-font-weight: bold");
        chosenRoleInfo.getChildren().add(availableRolesToAssign);
        String flowsToAssignUrl = HttpUrl.parse(FLOW_LIST)
                .newBuilder()
                .build()
                .toString();
        Response flowsResponse = HttpClientUtil.run(flowsToAssignUrl);
        Gson gson = new Gson();
        List<String> flowsList = gson.fromJson(flowsResponse.body().charStream(), List.class);
        for(String flow : flowsList)
        {

            RadioButton radioButton = new RadioButton(flow);
            if((role.getFlowsAllowed().contains(flow)&&firstTime) || selectedFlows.contains(flow))
            {
                //mark the radio button as selected
                radioButton.setSelected(true);
                selectedFlows.add(flow);
            }
            else {
                radioButton.setSelected(false);
            }
            radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedFlows.add(flow);
                } else {
                    selectedFlows.remove(flow);
                }
            });
            if(role.getRoleName().equals("All Flows")||role.getRoleName().equals("Read Only"))
            {
                chosenRoleInfo.getChildren().add(new Text(flow));
            }
            else
                chosenRoleInfo.getChildren().add(radioButton);
            if(role.getFlowsAllowed().contains(flow))
            {
                Text assigned = new Text("ASSIGNED");
                assigned.setStyle("-fx-font-weight: bold; -fx-fill: green");
                chosenRoleInfo.getChildren().add(assigned);
                chosenRoleInfo.getChildren().add(new Text("\n"));
            }
            else {
                Text notAssigned = new Text("NOT ASSIGNED");
                notAssigned.setStyle("-fx-font-weight: bold; -fx-fill: red");
                chosenRoleInfo.getChildren().add(notAssigned);
                chosenRoleInfo.getChildren().add(new Text("\n"));
            }
        }
        Text availableUsersToAssign = new Text("Available users to assign: ");
        availableUsersToAssign.setStyle("-fx-font-weight: bold");
        chosenRoleInfo.getChildren().add(availableUsersToAssign);
        String usersToAssignUrl = HttpUrl.parse(USER_LIST)
                .newBuilder()
                .build()
                .toString();
        Response usersResponse = HttpClientUtil.run(usersToAssignUrl);
        List<String> usersList = gson.fromJson(usersResponse.body().charStream(), List.class);
        for(String user : usersList)
        {
            RadioButton radioButton = new RadioButton(user);
            if((role.getUsersAssigned().contains(user)&&firstTime) || selectedUsers.contains(user))
            {
                //mark the radio button as selected
                radioButton.setSelected(true);
                selectedUsers.add(user);
            }
            else {
                radioButton.setSelected(false);
            }
            radioButton.selectedProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    selectedUsers.add(user);
                } else {
                    selectedUsers.remove(user);
                }
            });
            chosenRoleInfo.getChildren().add(radioButton);
            if(role.getUsersAssigned().contains(user))
            {
                Text assigned = new Text("ASSIGNED");
                assigned.setStyle("-fx-font-weight: bold; -fx-fill: green");
                chosenRoleInfo.getChildren().add(assigned);
                chosenRoleInfo.getChildren().add(new Text("\n"));
            }
            else {
                Text notAssigned = new Text("NOT ASSIGNED");
                notAssigned.setStyle("-fx-font-weight: bold; -fx-fill: red");
                chosenRoleInfo.getChildren().add(notAssigned);
                chosenRoleInfo.getChildren().add(new Text("\n"));
            }
        }
        firstTime = false;
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
