package topClientScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import mta.course.java.stepper.menu.MenuVariables;

public class topClientController {
    private mainSceneClient.mainClientController mainClientController;
    private MenuVariables menuVariables;

    @FXML
    private Label clientNameLabel;
    @FXML
    private MenuItem annimationToggle;

    @FXML
    private Label clientName;

    @FXML
    private Label isManagerLabel;

    @FXML
    private Label isManager;

    @FXML
    private Label assignedRolesLabel;

    @FXML
    private Label assignedRoles;

    @FXML
    private MenuButton menuBar;

    @FXML
    private Button flowsDefButton;

    @FXML
    private Button flowsExecutionButton;

    @FXML
    private Button executionsHistoryButton;
    public void setMenuVariables(MenuVariables menuVariables) {
        this.menuVariables = menuVariables;
    }
    public void setClientName(String clientName) {
        this.clientName.setText(clientName);
    }
    public void setIsManager(String isManager) {
        this.isManager.setText(isManager);
    }
    public void setAssignedRoles(String assignedRoles) {
        this.assignedRoles.setText(assignedRoles);
    }
    @FXML
    public void switchToShowFlowScene(ActionEvent event) {
        if (mainClientController != null) {
            mainClientController.switchToShowFlowScene(event);
        }
    }
    @FXML
    public void switchToExecutionScene(ActionEvent event) {
        if (mainClientController != null) {
            mainClientController.switchToExecutionScene(event);
        }
    }
    @FXML
    public void switchToHistoryScene(ActionEvent event) {
        if (mainClientController != null) {
            mainClientController.switchToHistoryScene(event);
        }
    }

    public void setMainSceneClientController(mainSceneClient.mainClientController mainSceneClientController)
    {
        this.mainClientController = mainSceneClientController;
    }
    public String getClientName() {
        return clientName.getText();
    }
}
