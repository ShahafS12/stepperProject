package mainSceneClient;

import StatisticsScene.statisticsController;
import executionScene.executionController;
import historyScene.historySceneController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.menu.MenuVariables;
import showFlowScene.ShowFlowController;
import topClientScene.topClientController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class mainSceneClientController
{
    @FXML private topClientController topClientComponentController;

    @FXML private ShowFlowController ShowFlowComponentController;

    @FXML private BorderPane mainBorder;
    private executionScene.executionController executionController;
    private MenuVariables menuVariables;
    private historyScene.historySceneController historySceneController;
    @FXML
    public void initialize()
    {
        if(topClientComponentController != null && ShowFlowComponentController != null)
        {
            topClientComponentController.setMainSceneClientController(this);
            ShowFlowComponentController.setMainSceneClientController(this);
        }
    }
    public void updateUserName(String userName)
    {
        topClientComponentController.setClientName(userName);
    }
    public void switchToExecutionScene(ActionEvent event){
        if(executionController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/executionScene/executionScene.fxml"));
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
        if(menuVariables == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Please load a stepper first!");
            alert.showAndWait();
        }
        else {
            if (historySceneController == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/historyScene/historySceneBuilder.fxml"));
                try {
                    Parent executionRoot = loader.load();
                    historySceneController = loader.getController();
                    //historySceneController.setMainController(this);
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
    public void refreshExecutionScene(){
        if(executionController != null) {
            executionController.refresh();
        }
    }
    public void setExecutionController(executionController executionController)
    {
        this.executionController = executionController;
    }
    public void switchToShowFlowScene(ActionEvent event){
        try {
            AnchorPane view = ShowFlowComponentController.getShowFlowAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public FlowDefinition getFlowDefinition(String newValue)
    {
        return menuVariables.getStepper().getFlowDefinition(newValue);
    }
    public MenuVariables getMenuVariables()
    {
        return menuVariables;
    }

    public void setMenuVariables(MenuVariables variables){
        this.menuVariables = variables;
    }
    public void setShowFlowComponentController(){
        ShowFlowComponentController.setFlowsList(menuVariables.getFlowNames());
    }

    public void close()
    {
        if(executionController != null)
        {
            if(menuVariables != null)
                menuVariables.shutdownExecutorService();
            if(executionController != null)
                executionController.shutdownExecutorService();
        }
    }
}
