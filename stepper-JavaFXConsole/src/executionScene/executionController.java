package executionScene;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.SingleStepExecutionData;

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
    @FXML
    private TextFlow stepDetails;
    private List<Control> mandatoryInputs;
    private List<Control> optionalInputs;
    private List<InputWithStepName> outputs;
    private mainScene.mainController mainController;
    private FlowDefinition chosenFlow;
    private List<SingleStepExecutionData> executionData;
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
        stepDetails.getChildren().clear();
        stepDetails.getChildren().add(new Text(newValue));
    }
    private void handleStepSelection(SingleStepExecutionData newValue) {
        stepDetails.getChildren().clear();
        stepDetails.getChildren().add(new Text("Duration: " + String.format("%.2f", newValue.getDuration()) + " ms\n"));
        stepDetails.getChildren().add(new Text("Status: " + newValue.getSuccess() + "\n"));
        stepDetails.getChildren().add(new Text("Summary Line: " + newValue.getSummaryLine()));
        stepDetails.getChildren().add(new Text("\n"));
        Text logsText = new Text("Logs: \n");
        logsText.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        stepDetails.getChildren().add(logsText);
        for (String log : newValue.getLogs()) {
            String[] logParts = log.split("\\|");
            for(String logPart : logParts)
                stepDetails.getChildren().add(new Text(logPart+"\n"));
        }
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
        Label mandatoryInputsLabel = new Label("Mandatory Inputs:");
        mandatoryInputsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        inputsGridPane.addRow(row,mandatoryInputsLabel);
        row++;
        row++;
        for (InputWithStepName input : chosenFlow.getMandatoryInputs()) {
            inputsGridPane.addRow(row, new javafx.scene.control.Label(input.getDataDefinitionDeclaration().userString()), mandatoryInputs.get(currentMandatoryInputs));
            row++;
            currentMandatoryInputs++;
        }
        Label optionalInputsLabel = new Label("Optional Inputs:");
        optionalInputsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        inputsGridPane.addRow(row,optionalInputsLabel);
        row++;
        row++;
        for (InputWithStepName input : chosenFlow.getOptionalInputs()) {
            inputsGridPane.addRow(row, new javafx.scene.control.Label(input.getDataDefinitionDeclaration().userString()), optionalInputs.get(currentOptionalInputs));
            row++;
            currentOptionalInputs++;
        }
        //inputsGridPane.add(executeButton, 2, row);
        //executeButton.setDisable(true);//TODO: change to false when all mandatory inputs are filled
    }
    public void pupulateCurrentExecutionSteps(){
        currentExecutionSteps.getItems().clear();
        for(SingleStepExecutionData step : executionData){
            currentExecutionSteps.getItems().add(step.getStepName());
        }
        if(currentExecutionSteps.getItems().size() > 0){
            currentExecutionSteps.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if(newValue != null){
                    handleStepSelection(executionData.get(currentExecutionSteps.getSelectionModel().getSelectedIndex()));;
                }
            });
        }
    }

    @FXML
    public void executeFlow(ActionEvent event) {
        if (executionData != null)
            executionData.clear();
        else
            executionData = new ArrayList<>();

        // Create a Task to execute the executeFlowUI() method
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                FLowExecutor fLowExecutor = new FLowExecutor();
                fLowExecutor.executeFlowUI(mainController.getMenuVariables().getFlowExecutionMap().get(1),
                        mandatoryInputs, optionalInputs, outputs, executionData);
                return null;
            }
        };

        // Set up the completion handler when the task is finished
        Timer timer = new Timer();
        int interval = 1000; // Interval in milliseconds
        task.setOnSucceeded(e -> {
            // This code will run on the JavaFX application thread
            timer.cancel();
            pupulateCurrentExecutionSteps();
        });

        // Set up the exception handler if an exception occurs during the task
        task.setOnFailed(e -> {
            // This code will run on the JavaFX application thread
            Throwable exception = task.getException();
            // Handle the exception appropriately
        });

        // Execute the task in a separate thread
        Thread thread = new Thread(task);
        thread.start();


        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    // This code will run on the JavaFX application thread
                    System.out.println("Updating current execution steps");
                    pupulateCurrentExecutionSteps();
                });
            }
        }, interval, interval);
        FLowExecutor fLowExecutor = new FLowExecutor();
        fLowExecutor.executeFlowUI(mainController.getMenuVariables().getFlowExecutionMap().get(2),mandatoryInputs,optionalInputs,outputs,executionData);
        timer.cancel();
        pupulateCurrentExecutionSteps();
    }

    public void continuation(ActionEvent event)
    {
    }
}
