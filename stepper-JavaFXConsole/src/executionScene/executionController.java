package executionScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

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
    private List<Control> mandatoryInputs;
    private List<Control> optionalInputs;
    private List<InputWithStepName> outputs;
    private mainScene.mainController mainController;
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
    }
    public void setMainController(mainScene.mainController mainController){
        this.mainController = mainController;
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
        mandatoryInputs = new ArrayList<>();
        optionalInputs = new ArrayList<>();
        this.outputs = chosenFlow.getOutputs();
        int currentMandatoryInputs = 0;
        int currentOptionalInputs = 0;
        for(InputWithStepName input : chosenFlow.getMandatoryInputs()){
            if(input.getDataDefinitionDeclaration().dataDefinition().getType()==Number.class)
                mandatoryInputs.add(new javafx.scene.control.Spinner<Integer>(0,100,0,1));
            else
                mandatoryInputs.add(new javafx.scene.control.TextField());
        }
        for(InputWithStepName input : chosenFlow.getOptionalInputs()){
            if(input.getDataDefinitionDeclaration().dataDefinition().getType()==Integer.class)
                optionalInputs.add(new javafx.scene.control.Spinner<Integer>(0,100,0,1));
            else
                optionalInputs.add(new javafx.scene.control.TextField());

        }
        inputsGridPane.addRow(row,new javafx.scene.control.Label("Mandatory Inputs:"));
        row++;
        row++;
        for (InputWithStepName input : chosenFlow.getMandatoryInputs()) {
            inputsGridPane.addRow(row, new javafx.scene.control.Label(input.getDataDefinitionDeclaration().userString()), mandatoryInputs.get(currentMandatoryInputs));
            //inputsGridPane.addRow(row,new javafx.scene.control.Label(input.getDataDefinitionDeclaration().userString()),
                    //new javafx.scene.control.Spinner<Integer>(0,100,0,1));
            row++;
            currentMandatoryInputs++;
        }
        inputsGridPane.addRow(row,new javafx.scene.control.Label("Optional Inputs:"));
        row++;
        row++;
        for (InputWithStepName input : chosenFlow.getOptionalInputs()) {
            inputsGridPane.addRow(row, new javafx.scene.control.Label(input.getDataDefinitionDeclaration().userString()), optionalInputs.get(currentOptionalInputs));
            row++;
            currentOptionalInputs++;
        }
        inputsGridPane.add(executeButton, 2, row);
        //executeButton.setDisable(true);//TODO: change to false when all mandatory inputs are filled
    }

    @FXML
    public void executeFlow(ActionEvent event)
    {
        Timer timer = new Timer();
        int interval = 100; // Interval in milliseconds

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //System.out.println("Hello");
            }
        }, interval, interval);
        FLowExecutor fLowExecutor = new FLowExecutor();
        fLowExecutor.executeFlowUI(mainController.getMenuVariables().getFlowExecutionMap().get(1),mandatoryInputs,optionalInputs,outputs);
        //print hello every 100 ms
        timer.cancel();
    }

    public void continuation(ActionEvent event)
    {
    }
}
