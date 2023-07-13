package mainSceneAdmin;

import adapters.*;
import api.HttpStatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import showFlowScene.ShowFlowController;
import topScene.topAdminController;
import StatisticsScene.statisticsController;
import executionScene.executionController;
import historyScene.historySceneController;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.List;
import java.util.Map;

import javafx.util.Duration;

import static com.sun.deploy.ui.UIFactory.showErrorDialog;
import static util.Constants.*;

public class mainAdminController implements HttpStatusUpdate
{
    @FXML private topAdminController topComponentController;
    @FXML private ShowFlowController ShowFlowComponentController;

    @FXML private BorderPane mainBorder;
    private statisticsController statisticsController;
    private FlowDefinition toReturn;
    private executionController executionController;
    private MenuVariables menuVariables;
    private historySceneController historySceneController;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    private boolean animationToggle = true;

    @FXML
    public void initialize() {
        if (topComponentController != null && ShowFlowComponentController != null) {
            topComponentController.setMainController(this);
            ShowFlowComponentController.setMainController(this);
            ShowFlowComponentController.setHttpStatusUpdate(this);
        }
        ShowFlowComponentController.autoUpdatesProperty().bind(topComponentController.autoUpdatesProperty());
    }
    public void setActive(){
        ShowFlowComponentController.startListRefresher();
    }

    @FXML
    void loadStepperFromXml(ActionEvent event) {
        topComponentController.loadStepperFromXml(event);
        ShowFlowComponentController.setFlowsList(menuVariables.getFlowNames());
    }
    public void setShowFlowComponentController(){
        ShowFlowComponentController.setFlowsList(menuVariables.getFlowNames());
    }

    public MenuVariables getMenuVariables()
    {
        return menuVariables;
    }

    public void setMenuVariables(MenuVariables variables){
        this.menuVariables = variables;
        if(statisticsController != null)
            statisticsController.setFlowExecutionsStatistics(menuVariables.getFlowExecutionsStatisticsMap());
    }

    public FlowDefinition getFlowDefinition(String newValue)
    {
        String finalUrl = HttpUrl.parse(GET_FLOW_DEFINITION)
                .newBuilder()
                .addQueryParameter("flowName", newValue)
                .build()
                .toString();
        updateHttpLine("Getting flow definition from server for" + newValue);
        Response response = HttpClientUtil.run(finalUrl);
        try {
            if(response.code() != 200){
                Platform.runLater(() -> {
                    try {
                        errorMessageProperty.set("Something went wrong: " + response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            } else {
                System.out.println("Got flow definition");
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                        .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                        .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                        .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                        .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                        .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
                        .create();
                return gson.fromJson(response.body().string(), FlowDefinitionImpl.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void switchToStatisticsScene(ActionEvent event){
        if(menuVariables == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please load a stepper first!");
            alert.showAndWait();
        }
        else {
            if (statisticsController == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatisticsScene/StatisticsSceme.fxml"));
                try {
                    Parent statisticsRoot = loader.load();
                    statisticsController = loader.getController();
                    statisticsController.setMainController(this);
                    statisticsController.setFlowExecutionsStatistics(menuVariables.getFlowExecutionsStatisticsMap());
                    //statisticsController.setFlowExecutionsStatistics(menuVariables.get);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                statisticsController.populateFlowStatisticsTable();
                statisticsController.populateStepStatisticsTable();
                AnchorPane view = statisticsController.getStatisticsAnchorPane();
                mainBorder.setCenter(view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void refreshStatisticsScene(){
        if(statisticsController != null)
            statisticsController.refreshTables();
    }
    public void switchToShowFlowScene(ActionEvent event){
        try {
            AnchorPane view = ShowFlowComponentController.getShowFlowAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatisticsController(statisticsController statisticsController)
    {
        this.statisticsController = statisticsController;
    }
    public void setExecutionController(executionController executionController)
    {
        this.executionController = executionController;
    }
    public void switchToExecutionScene(ActionEvent event, FlowDefinition chosenFlow){
        if(executionController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/executionScene/executionScene.fxml"));
            try {
                Parent executionRoot = loader.load();
                executionController = loader.getController();
                executionController.setMainController(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            executionController.setChosenFlow(chosenFlow);
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshExecutionScene(){
        if(executionController != null) {
            executionController.refresh();
        }
    }
    public boolean getAnimationToggle(){
        return this.animationToggle;
    }
    public void switchToExecutionSceneWithContinuation(ActionEvent event, FlowDefinition chosenFlow, Map<String, List<String>> continuation){
        //don't need to check if executionController is null because it is not null when we get here
        try {
            executionController.setChosenFlow(chosenFlow, continuation);
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setReRunFlow(ActionEvent event, FlowDefinition chosenFlow, Map<String, Object> userInputsMap){
        try {
            executionController.setReRunFlow(chosenFlow, userInputsMap);
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void switchToExecutionScene(ActionEvent event){
        if(executionController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(EXECUTION_PAGE_FXML_RESOURCE_LOCATION));
            try {
                Parent executionRoot = loader.load();
                executionController = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showGifForDuration(String gifPath, Duration duration) {
        if(!animationToggle)//if animation is off
            return;
        Image gifImage = new Image(gifPath);
        ImageView gifImageView = new ImageView(gifImage);

        // Adjust the properties of the ImageView if needed
        double height = mainBorder.getHeight();
        double width = mainBorder.getWidth();
        gifImageView.setFitWidth(width);
        gifImageView.setFitHeight(height);
        //gifImageView.setLayoutX(width / 2 - 75);
        //gifImageView.setLayoutY(height / 2 - 75);

        mainBorder.getChildren().add(gifImageView);

        Timeline gifTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    // Start playing the GIF
                    gifImageView.setVisible(true);
                }),
                new KeyFrame(duration, e -> {
                    // Stop playing the GIF and remove it from the parentPane
                    gifImageView.setVisible(false);
                    mainBorder.getChildren().remove(gifImageView);
                })
        );

        gifTimeline.play();
    }
    public void populateStepStatisticsTable(){
        statisticsController.populateStepStatisticsTable();
    }
    public void switchToHistoryScene(ActionEvent event){
        if(menuVariables == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please load a stepper first!");
            alert.showAndWait();
        }
        else {
            if (historySceneController == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(HISTORY_PAGE_FXML_RESOURCE_LOCATION));
                try {
                    Parent executionRoot = loader.load();
                    historySceneController = loader.getController();
                    historySceneController.setMainController(this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                AnchorPane view = historySceneController.getHistoryAnchorPane();
                mainBorder.setCenter(view);
                historySceneController.setTableInHistory();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void setHistoryController(historyScene.historySceneController historySceneController) { this.historySceneController = historySceneController;}

    public void handleAnimationsToggle(ActionEvent event)
    {
        this.animationToggle = !this.animationToggle;
    }

    public void close()
    {
        if(menuVariables != null)
            menuVariables.shutdownExecutorService();
        if(executionController != null)
            executionController.shutdownExecutorService();
    }

    public void updateHttpLine(String data)
    {
        System.out.println("updateHttpLine");
        //todo - check if this is actually needed
    }
}
