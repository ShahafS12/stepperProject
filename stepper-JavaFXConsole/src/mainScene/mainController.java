package mainScene;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
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
import mta.course.java.stepper.menu.MenuVariables;
import showFlowScene.ShowFlowController;
import topScene.topController;
import StatisticsScene.statisticsController;
import executionScene.executionController;
import historyScene.historySceneController;

import java.io.IOException;
import javafx.util.Duration;

import static com.sun.deploy.ui.UIFactory.showErrorDialog;

public class mainController {
    @FXML private topController topComponentController;
    @FXML private ShowFlowController ShowFlowComponentController;

    @FXML private BorderPane mainBorder;
    private statisticsController statisticsController;
    private executionController executionController;
    private MenuVariables menuVariables;
    private historySceneController historySceneController;

    @FXML
    public void initialize() {
        if (topComponentController != null && ShowFlowComponentController != null) {
            topComponentController.setMainController(this);
            ShowFlowComponentController.setMainController(this);
        }
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
        return menuVariables.getStepper().getFlowDefinition(newValue);
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../StatisticsScene/statisticsSceme.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../executionScene/executionScene.fxml"));
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
    public void switchToExecutionScene(ActionEvent event){
        if(executionController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../executionScene/executionScene.fxml"));
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("../historyScene/historySceneBuilder.fxml"));
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
}
