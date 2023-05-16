package showFlowScene;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import mainScene.mainController;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;

import java.util.List;

public class ShowFlowController {
    private mainScene.mainController mainController;

    @FXML
    private ListView<String> flowsList;

    @FXML
    private TextArea chosenFlowData;

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

    private void handleFlowSelection(String newValue)
    {
        FlowDefinition flow = mainController.getFlowDefinition(newValue);
        setChosenFlowData(flow);
    }

    public void setChosenFlowData(FlowDefinition flow) {
        chosenFlowData.clear();

        // Append flow details to the outputTextArea
        chosenFlowData.appendText("Flow Name: " + flow.getName() + "\n");
        chosenFlowData.appendText("Description: " + flow.getDescription() + "\n");
        chosenFlowData.appendText("Flow Formal Outputs: " + flow.getFlowFormalOutputs() + "\n");
        chosenFlowData.appendText("read-only: " + flow.isReadOnly() + "\n");
        List<StepUsageDeclaration> steps = flow.getFlowSteps();
        chosenFlowData.appendText("\nSteps: \n");
        for (int i = 0; i < steps.size(); i++) {
            StepUsageDeclaration step = steps.get(i);
            if (step.getStepName() != step.getFinalStepName())
                chosenFlowData.appendText(step.getStepName() + "," + step.getFinalStepName() + "\n");
            else
                chosenFlowData.appendText(step.getStepName() + "\n");
            chosenFlowData.appendText("Is readOnly: " + step.getStepDefinition().isReadonly() + "\n\n");
        }
        //TODO print flow free inputs and outputs
    }

}