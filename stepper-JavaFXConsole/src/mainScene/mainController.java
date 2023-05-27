package mainScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
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
        if(statisticsController == null) {
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
    public void switchToHistoryScene(ActionEvent event){
        if(historySceneController == null) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
