package executionScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

public class executionController {
    @FXML
    private AnchorPane executionAnchorPane;

    @FXML
    private GridPane inputsGridPane;

    @FXML
    private Button executeButton;

    @FXML
    private ListView<String> currentExecutionSteps;

    @FXML
    private Button flowContinuationButton;
    private FlowDefinition chosenFlow;
    private int currentAmountOfMandatoryInputs;
    public AnchorPane getExecutionAnchorPane(){
        return executionAnchorPane;
    }
    public void Initialize(FlowDefinition chosenFlow){
        this.chosenFlow = chosenFlow;
        populateInputsGridPane();
        if(currentExecutionSteps != null){
            currentExecutionSteps.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null){
                    handleStepSelection(newValue);
                }
            });
        }
        currentAmountOfMandatoryInputs = chosenFlow.getFlowFreeInputs().size();
        executeButton.setDisable(true);
    }

    private void handleStepSelection(String newValue)
    {
        //TODO: implement
    }
    public void setChosenFlow(FlowDefinition chosenFlow){
        this.chosenFlow = chosenFlow;
        populateInputsGridPane();
    }
    private void populateInputsGridPane(){
        inputsGridPane.getChildren().clear();
        int row = 0;
        inputsGridPane.addRow(row, new javafx.scene.control.Label("Input"), new javafx.scene.control.Label("Value"));
        row++;
        for (DataDefinitionDeclaration input : chosenFlow.getFlowFreeInputs()) {
            inputsGridPane.addRow(row, new javafx.scene.control.Label(input.userString()), new javafx.scene.control.TextField());
            row++;
        }
        inputsGridPane.add(executeButton, 2, row);
        executeButton.setDisable(true);//TODO: change to false when all mandatory inputs are filled
    }

    public void executeFlow(ActionEvent event)
    {
    }

    public void continuation(ActionEvent event)
    {
    }
}
