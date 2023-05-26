package topScene;

import dataloader.LoadXMLFile;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import mainScene.mainController;
import mta.course.java.stepper.flow.execution.FlowExecution;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.stepper.StepperDefinition;

import java.io.File;
import java.util.ArrayList;

import static com.sun.deploy.ui.UIFactory.showErrorDialog;

public class topController {
    private mainController mainController;
    private MenuVariables menuVariables;

    @FXML
    private Button LoadXMLButton;

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

    @FXML
    public void loadStepperFromXml(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose XML file");
        File file = fileChooser.showOpenDialog(LoadXMLButton.getScene().getWindow());
        try {
            MenuVariables newMenuVariables = new MenuVariables();
            LoadXMLFile loadXMLFile = new LoadXMLFile();
            StepperDefinition newStepper = (loadXMLFile.loadXMLFile(file));
            newMenuVariables.setStepper(newStepper);
            ArrayList<String> newFlowNames = new ArrayList<>();
            newFlowNames.addAll(newStepper.getFlowNames());//add the latest names of the flows
            newMenuVariables.setFlowNames(newFlowNames);
            //flowIdMap.put(flowNames.get(flowNames.size()-1),uniqueFlowIdCounter);
            for (int i = 0; i < newMenuVariables.getFlowNames().size(); i++) {
                FlowExecution flowExecution = new FlowExecution(newMenuVariables.getFlowNames().get(i), newStepper.getFlowDefinition(newFlowNames.get(i)));
                newMenuVariables.putFlowExecutionMap(newMenuVariables.getUniqueFlowIdCounter(), flowExecution);
                newMenuVariables.putFlowExecutionMapFromFlowName(newMenuVariables.getFlowNames().get(i), flowExecution);
                newMenuVariables.upuniqueFlowIdCounter();
            }
            this.menuVariables = newMenuVariables;
            currentXMLLabel.setText(file.getAbsolutePath());
            mainController.setMenuVariables(menuVariables);
            mainController.setShowFlowComponentController();
        } catch (Exception e) {
            showErrorDialog("Invalid XML file" , e.getMessage());
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

    public void setMainController(mainController mainController) {
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
    public void switchToExecutionScene(ActionEvent event)
    {
        mainController.switchToExecutionScene(event);
    }
}