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
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import mainScene.mainController;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.DataDefinitionDeclarationImpl;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShowFlowController {
    private mainScene.mainController mainController;
    @FXML
    private AnchorPane showFlowAnchorPane;

    @FXML
    private ListView<String> flowsList;
    @FXML
    private Button executeButton;

    @FXML
    private VBox chosenFlowData;
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
        Text stepsText = new Text("Steps: \n");
        stepsText.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        chosenFlowData.getChildren().add(stepsText);
        HBox[] hBoxes = new HBox[steps.size()];
        Button[] buttons = new Button[steps.size()];
        for (int i = 0; i < steps.size(); i++) {
            StepUsageDeclaration step = steps.get(i);
            if (step.getStepName() != step.getFinalStepName()) {
                chosenFlowData.getChildren().add(new Text(step.getStepName() + "," + step.getFinalStepName()));
            }
            else
                chosenFlowData.getChildren().add(new Text(step.getStepName()));
            hBoxes[i] = new HBox();
            buttons[i] = new Button("Show Step Details" + "\n");
            buttons[i].setOnAction(event -> {
                chosenFlowData.getChildren().clear();
                chosenFlowData.getChildren().add(new Text(step.getStepName() + "," + step.getFinalStepName()));
                chosenFlowData.getChildren().add(new Text("\n" + "Inputs (Name, Necessity/Connected to output):" + "\n"));
                Map<String, String> allInputs = flow.getAllFlowInputsWithNeccecity();
                Map<String, String> flowAlias = flow.getAllFlowLevelAlias();
                Map<String, String> customMap = flow.getCustomMapping();

                for (Map.Entry<String, String> entry : allInputs.entrySet()) {
                    boolean flag = false;

                    String key = entry.getKey(); // Step name + "." + input name
                    String value = entry.getValue(); // neccecity

                    String[] stepNamePlusInputName = key.split("\\.");

                    if (stepNamePlusInputName[0].equals(step.getFinalStepName())){
                        // run on all values in customMap
                        for (Map.Entry<String, String> entry2 : customMap.entrySet()) {
                            String keyCustom = entry2.getKey();
                            String valueCustom = entry2.getValue();
                            if (valueCustom.equals(stepNamePlusInputName[0] + "." + stepNamePlusInputName[1])){
                                flag = true;
                                String[] tmp = keyCustom.split("\\.");
                                chosenFlowData.getChildren().add(new Text( stepNamePlusInputName[1] + ", Output step: " + tmp[0] + ", Output: " + tmp[1] + "\n"));
                            }
                        }
                        if (!flag && flowAlias.containsKey(stepNamePlusInputName[0] + "." + stepNamePlusInputName[1])){
                            String aliasInput  =  flowAlias.get(stepNamePlusInputName[0] + "." + stepNamePlusInputName[1]);
                            String[] tmp = aliasInput.split("\\.");
                            chosenFlowData.getChildren().add(new Text( stepNamePlusInputName[1] + ", alias: " + tmp[1] + ", " + value + "\n"));
                        }
                        else if (!flag){
                        chosenFlowData.getChildren().add(new Text( stepNamePlusInputName[1] + ", " + value +  "\n"));
                        }
                    }
                }
                chosenFlowData.getChildren().add(new Text("\n" + "Outputs (Connected to Input step):" + "\n"));
                for (InputWithStepName output : flow.getOutputs()){
                    boolean flagOutput = false;
                    if (output.getStepName().equals(step.getFinalStepName())){
                        for (Map.Entry<String, String> entry2 : customMap.entrySet()) {
                            String keyCustom = entry2.getKey();
                            String valueCustom = entry2.getValue();
                            if (keyCustom.equals(output.getStepName() + "." + output.getDataDefinitionDeclaration().getName())){
                                flagOutput = true;
                                String[] tmp = valueCustom.split("\\.");
                                chosenFlowData.getChildren().add(new Text( output.getDataDefinitionDeclaration().getName() + ", Input step: " + tmp[0] + ", Input: " + tmp[1] + "\n"));
                            }
                        }
                        if (!flagOutput && flowAlias.containsKey(output.getStepName() + "." + output.getDataDefinitionDeclaration().getName())){
                            String aliasInput  =  flowAlias.get(output.getStepName() + "." + output.getDataDefinitionDeclaration().getName());
                            String[] tmp = aliasInput.split("\\.");
                            chosenFlowData.getChildren().add(new Text( output.getDataDefinitionDeclaration().getName() + ", alias: " + tmp[1] + "\n"));
                        }
                        else if (!flagOutput){
                            chosenFlowData.getChildren().add(new Text( output.getDataDefinitionDeclaration().getName() + "\n"));
                        }
                    }
                }
                flowsList.getSelectionModel().clearSelection();
            });
            hBoxes[i].getChildren().add(buttons[i]);
            chosenFlowData.getChildren().add(new Text("Is readOnly: " + step.getStepDefinition().isReadonly()));
            chosenFlowData.getChildren().add(hBoxes[i]);
            chosenFlowData.getChildren().add(new Text("\n"));
        }
    }
}