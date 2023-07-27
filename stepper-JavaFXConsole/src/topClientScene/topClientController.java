package topClientScene;

import api.HttpStatusUpdate;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import mta.course.java.stepper.menu.MenuVariables;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.REFRESH_RATE;

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
    private HttpStatusUpdate httpStatusUpdate;
    private TimerTask topRefresher;
    private Timer timer;
    public void setMenuVariables(MenuVariables menuVariables) {
        this.menuVariables = menuVariables;
    }
    public void setClientName(String clientName) {
        this.clientName.setText(clientName);
    }
    public void setIsManager(boolean isManager) {
        if (isManager)
            this.isManager.setText("Yes");
        else
            this.isManager.setText("No");
    }
    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate) {
        this.httpStatusUpdate = httpStatusUpdate;
    }
    public void setAssignedRoles(String assignedRoles) {
        this.assignedRoles.setText(assignedRoles);
    }
    public void setAssignedRoles(List<String> assignedRoles) {
        StringBuilder sb = new StringBuilder();
        for (String role : assignedRoles) {
            sb.append(role).append(", ");
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 2, sb.length());
        }
        this.assignedRoles.setText(sb.toString());
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
    public void StartTopRefresher() {
        topRefresher = new isManagerRefresher(
                this.clientName.getText(),
                httpStatusUpdate::updateHttpLine,
                this::setIsManager,
                this::setAssignedRoles);
        timer = new Timer();
        timer.schedule(topRefresher, REFRESH_RATE, REFRESH_RATE);
    }
}
