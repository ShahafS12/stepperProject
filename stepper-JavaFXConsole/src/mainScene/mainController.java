package mainScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.menu.MenuVariables;
import showFlowScene.ShowFlowController;
import showFlowScene.statisticsController;
import topScene.topController;

public class mainController {
    @FXML private topController topComponentController;
    @FXML private ShowFlowController ShowFlowComponentController;

    @FXML private BorderPane mainBorder;
    private showFlowScene.statisticsController statisticsController;
    private MenuVariables menuVariables;
    private Parent root;
    private Scene scene;
    private Stage stage;

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
    public void setMenuVariables(MenuVariables variables){
        this.menuVariables = variables;
    }

    public FlowDefinition getFlowDefinition(String newValue)
    {
        return menuVariables.getStepper().getFlowDefinition(newValue);
    }
    public void switchToStatisticsScene(ActionEvent event){
        try {
            AnchorPane view = FXMLLoader.load(getClass().getResource("../showFlowScene/statisticsSceme.fxml"));
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void switchToShowFlowScene(ActionEvent event){
        try {
            AnchorPane view = FXMLLoader.load(getClass().getResource("../showFlowScene/showFlow.fxml"));
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatisticsController(statisticsController statisticsController)
    {
        this.statisticsController = statisticsController;
    }
}
