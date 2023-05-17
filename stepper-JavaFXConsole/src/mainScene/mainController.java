package mainScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.menu.MenuVariables;
import showFlowScene.ShowFlowController;
import topScene.topController;

public class mainController {
    @FXML private topController topComponentController;
    @FXML private ShowFlowController ShowFlowComponentController;
    private MenuVariables menuVariables;

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
}