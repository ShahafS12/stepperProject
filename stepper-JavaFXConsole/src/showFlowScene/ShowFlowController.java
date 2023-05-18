package showFlowScene;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import mainScene.mainController;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;

import java.util.List;

public class ShowFlowController {
    private mainScene.mainController mainController;

    @FXML
    private ListView<String> flowsList;

    @FXML
    private TextFlow chosenFlowData;

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
        if(flow == null)
            return;
        if(chosenFlowData!=null)
            chosenFlowData.getChildren().clear();

        // Append flow details to the outputTextArea
        Text flowName = new Text("Flow Name: " + flow.getName() + "\n");
        flowName.setStyle("-fx-font-weight: bold");
        chosenFlowData.getChildren().add(flowName);
        chosenFlowData.getChildren().add(new Text("Description: " + flow.getDescription() + "\n"));
        chosenFlowData.getChildren().add(new Text("Flow Formal Outputs: " + flow.getFlowFormalOutputs() + "\n"));
        chosenFlowData.getChildren().add(new Text("read-only: " ));
        if (flow.isReadonly())
            chosenFlowData.getChildren().add(new Text("\u2713\n"));
        else
            chosenFlowData.getChildren().add(new Text("\u2717\n"));
        List<StepUsageDeclaration> steps = flow.getFlowSteps();
        Text stepsText = new Text("\nSteps: \n");
        stepsText.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        chosenFlowData.getChildren().add(stepsText);
        for (int i = 0; i < steps.size(); i++) {
            StepUsageDeclaration step = steps.get(i);
            if (step.getStepName() != step.getFinalStepName())
                chosenFlowData.getChildren().add(new Text(step.getStepName() + "," + step.getFinalStepName() + "\n"));
            else
                chosenFlowData.getChildren().add(new Text(step.getStepName() + "\n"));
            chosenFlowData.getChildren().add(new Text("Is readOnly: " + step.getStepDefinition().isReadonly() + "\n\n"));
        }
        //TODO print flow free inputs and outputs
    }

}