package topScene;

import com.google.gson.Gson;
import dataloader.LoadXMLFile;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import mainSceneAdmin.mainAdminController;
import mta.course.java.stepper.flow.definition.api.Continuation;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.stepper.StepperDefinition;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.sun.deploy.ui.UIFactory.showErrorDialog;
import static util.Constants.ADD_XML_PAGE;

public class topAdminController
{
    private mainAdminController mainController;
    private MenuVariables menuVariables;
    private final BooleanProperty autoUpdates;

    @FXML
    private Button LoadXMLButton;
    @FXML
    private MenuItem annimationToggle;

    @FXML
    private Label currentXMLLabel;

    @FXML
    private Button flowsDefButton;
    @FXML
    private HBox mostTopBox;

    @FXML
    private Button flowsExecutionButton;

    @FXML
    private Button executionsHistoryButton;

    @FXML
    private Button statisticsButton;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    public topAdminController()
    {
        autoUpdates = new SimpleBooleanProperty();
    }

    @FXML
    public void loadStepperFromXml(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose XML file");
        File file = fileChooser.showOpenDialog(LoadXMLButton.getScene().getWindow());
        String finalUrl = HttpUrl.parse(ADD_XML_PAGE)
                .newBuilder()
                .addQueryParameter("xmlFile", file.getAbsolutePath())//todo check if this is the right way to send the file
                .build().toString();
        updateHttpStatusLine("New load request is launched for: " + finalUrl);
        HttpClientUtil.runAsync(finalUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage()
                ));
            }
            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() != 200){
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                }
                else {
                    String responseBody = response.body().string();
                    //httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);//todo this was at aviads code - check if needed
                    String[] flowNames = new Gson().fromJson(responseBody, String[].class);
                    //usersListConsumer.accept(Arrays.asList(usersNames));
                    mainController.setActive();
                }
            }
        });
    }

    private void updateHttpStatusLine(String data) {
        mainController.updateHttpLine(data);
    }

    @FXML
    void handleAnimationsToggle(ActionEvent event) {
        if (mainController != null) {
            mainController.handleAnimationsToggle(event);
        }
        if(mainController.getAnimationToggle()==true)
        {
            annimationToggle.setText("Turn off animations");
        }
        else
        {
            annimationToggle.setText("Turn on animations");
        }
    }

    private void showErrorDialog( String title, String message)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setMainController(mainAdminController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void switchToStatisticsScene(ActionEvent event) {
        mainController.switchToStatisticsScene( event);
    }
    @FXML
    public void switchToShowFlowScene(ActionEvent event) {
        mainController.switchToShowFlowScene( event);
    }
    @FXML
    public void switchToExecutionScene(ActionEvent event) { mainController.switchToExecutionScene(event);}
    @FXML
    public void switchToHistoryScene(ActionEvent event) { mainController.switchToHistoryScene(event); }

    public ReadOnlyBooleanProperty autoUpdatesProperty()
    {
        return autoUpdates;
    }
}