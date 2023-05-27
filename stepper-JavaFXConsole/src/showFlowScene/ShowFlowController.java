package showFlowScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import mainScene.mainController;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;

import java.net.URL;
import java.util.List;

public class ShowFlowController {
    private mainScene.mainController mainController;
    @FXML
    private AnchorPane showFlowAnchorPane;

    @FXML
    private ListView<String> flowsList;
    @FXML
    private Button executeButton;

    @FXML
    private TextFlow chosenFlowData;
    private Parent root;
    private Scene scene;
    private Stage stage;

    public void initialize() {
        if (flowsList != null) {
            flowsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    handleFlowSelection(newValue);
                }
            });
        }
    }
    @FXML
    void executeChosenFlow(ActionEvent event) {
        String chosenFlow = flowsList.getSelectionModel().getSelectedItem();
        if (chosenFlow != null) {
            FlowDefinition flow = mainController.getFlowDefinition(chosenFlow);
            mainController.switchToExecutionScene(event, flow);
        }
    }
    public FlowDefinition getChoosenFlow(){
        String chosenFlow = flowsList.getSelectionModel().getSelectedItem();
        if (chosenFlow != null) {
            FlowDefinition flow = mainController.getFlowDefinition(chosenFlow);
            return flow;
        }
        return null;
    }
    public AnchorPane getShowFlowAnchorPane(){
        return showFlowAnchorPane;
    }
    public void setMainController(mainController mainController) {
        this.mainController = mainController;
    }

    public void setFlowsList(List<String> flowsNames) {
        flowsList.getItems().clear();
        flowsList.getItems().addAll(flowsNames);
        flowsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleFlowSelection(newValue);
            }
        });
    }
    public void switchToStatisticsScene(){

    }

    private void handleFlowSelection(String newValue)
    {
        FlowDefinition flow = mainController.getFlowDefinition(newValue);
        setChosenFlowData(flow);
    }
    public void switchToStatisticsScene(ActionEvent event){
        try {
            URL url = getClass().getResource("statisticsSceme.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(url);
            root = fxmlLoader.load();
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void setChosenFlowData(FlowDefinition flow) {
        if(flow == null)
            return;
        if(chosenFlowData!=null)
            chosenFlowData.getChildren().clear();

        // Append flow details to the outputTextArea
        Text flowName = new Text("Flow Name: ");
        flowName.setStyle("-fx-font-weight: bold");
        chosenFlowData.getChildren().add(flowName);
        chosenFlowData.getChildren().add(new Text(flow.getName() + "\n"));
        Text description  = new Text("Description: ");
        description.setStyle("-fx-font-weight: bold");
        chosenFlowData.getChildren().add(description);
        chosenFlowData.getChildren().add(new Text(flow.getDescription() + "\n"));
        Text flowFormalInputs = new Text("Flow Formal Inputs: ");
        flowFormalInputs.setStyle("-fx-font-weight: bold");
        chosenFlowData.getChildren().add(flowFormalInputs);
        chosenFlowData.getChildren().add(new Text(flow.getFlowFormalOutputs() + "\n"));
        Text readOnly = new Text("Is readOnly: ");
        readOnly.setStyle("-fx-font-weight: bold");
        chosenFlowData.getChildren().add(readOnly);
        if (flow.isReadonly())
            chosenFlowData.getChildren().add(new Text("\u2713\n"));
        else
            chosenFlowData.getChildren().add(new Text("\u2717\n"));
        List<StepUsageDeclaration> steps = flow.getFlowSteps();
        Text stepsText = new Text("\nSteps: \n");
        stepsText.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        chosenFlowData.getChildren().add(stepsText);
        HBox[] hBoxes = new HBox[steps.size()];
        Button[] buttons = new Button[steps.size()];
        for (int i = 0; i < steps.size(); i++) {
            StepUsageDeclaration step = steps.get(i);
            if (step.getStepName() != step.getFinalStepName()) {
                chosenFlowData.getChildren().add(new Text(step.getStepName() + "," + step.getFinalStepName() + "\n\n\n"));
                hBoxes[i] = new HBox();
                buttons[i] = new Button("Show");
                buttons[i].setOnAction(event -> {
                    mainController.switchToHistoryScene(event);
                });
                hBoxes[i].getChildren().add(buttons[i]);
                chosenFlowData.getChildren().add(hBoxes[i]);
            }
            else
                chosenFlowData.getChildren().add(new Text("\n" +step.getStepName() + "\n"));
            chosenFlowData.getChildren().add(new Text("\nIs readOnly: " + step.getStepDefinition().isReadonly() + "\n\n"));
        }
        //TODO print flow free inputs and outputs
    }
}