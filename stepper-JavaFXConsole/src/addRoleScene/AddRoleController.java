package addRoleScene;


import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static util.Constants.*;

public class AddRoleController
{
    @FXML
    private TextField roleNameField;

    @FXML
    private TextField roleDescriptionField;

    @FXML
    private VBox assignedUsersBox;

    @FXML
    private VBox assignedFlowsBox;

    @FXML
    private Button createRoleButton;
    private List<String> usersList;
    private List<String> flowsList;
    public AddRoleController()
    {
        assignedUsersBox = new VBox();
        assignedFlowsBox = new VBox();
        String userListURL = HttpUrl.parse(USER_LIST)
                .newBuilder()
                .build()
                .toString();
        String flowListURL = HttpUrl.parse(FLOW_LIST)
                .newBuilder()
                .build()
                .toString();
        Response usersResponse = HttpClientUtil.run(userListURL);
        Response flowsResponse = HttpClientUtil.run(flowListURL);
        Gson gson = new Gson();
        usersList = gson.fromJson(usersResponse.body().charStream(), List.class);
        flowsList = gson.fromJson(flowsResponse.body().charStream(), List.class);
        if(usersList == null)
            usersList = new ArrayList<>();
        if(flowsList == null)
            flowsList = new ArrayList<>();
        populateComboBoxes();
    }
    public void initialize() {
        String userListURL = HttpUrl.parse(USER_LIST)
                .newBuilder()
                .build()
                .toString();
        String flowListURL = HttpUrl.parse(FLOW_LIST)
                .newBuilder()
                .build()
                .toString();
        Response usersResponse = HttpClientUtil.run(userListURL);
        Response flowsResponse = HttpClientUtil.run(flowListURL);
        Gson gson = new Gson();
        usersList = gson.fromJson(usersResponse.body().charStream(), List.class);
        flowsList = gson.fromJson(flowsResponse.body().charStream(), List.class);
        populateComboBoxes();
    }
    private void populateComboBoxes()
    {
        //populate the users combo box using the usersList
        for(String user : usersList)
        {
            assignedUsersBox.getChildren().add(new RadioButton(user));
        }
        //populate the flows combo box using the flowsList
        for(String flow : flowsList)
        {
            assignedFlowsBox.getChildren().add(new RadioButton(flow));
        }
    }
    @FXML
    public void createRole(ActionEvent event)
    {
        String roleName = roleNameField.getText();
        String roleDescription = roleDescriptionField.getText();
        List<String> flowsAllowed = assignedFlowsBox.getChildren().stream()
                .map(node -> (RadioButton) node)
                .filter(RadioButton::isSelected)
                .map(RadioButton::getText)
                .collect(Collectors.toList());
        List<String> usersAllowed = assignedUsersBox.getChildren().stream()
                .map(node -> (RadioButton) node)
                .filter(RadioButton::isSelected)
                .map(RadioButton::getText)
                .collect(Collectors.toList());
        Gson gson = new Gson();
        String[] jsonArray = {roleName, roleDescription, gson.toJson(flowsAllowed), gson.toJson(usersAllowed)};
        String json = gson.toJson(jsonArray);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), json);
        Request request = new Request.Builder()
                .url(CREATE_ROLE)
                .post(body)
                .build();
        HttpClientUtil.runAsync(request, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                try {
                    throw new Exception("Request failed."); // Throw exception if the request fails
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                if(response.code() == 200)
                {
                    System.out.println("Role created successfully.");

                }
                else
                {
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        try {
                            alert.setContentText("Role creation failed " + response.body().string() + " " + response.code());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        alert.showAndWait();
                    });
                }

            }
        });
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
