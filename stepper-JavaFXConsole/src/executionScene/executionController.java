package executionScene;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.FlowExecutionResult;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.impl.ZipperStep.ZipperEnumerator;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;

import java.util.*;

import java.util.concurrent.CountDownLatch;


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
    private Map<String, String> initialVal;
    private int currentFilledMandatoryInputs;
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
        executeButton.setDisable(true);
        int CurrenLisetenerIndex = 0;
        int row = 0;
        mandatoryInputs = new ArrayList<>();
        currentAmountOfMandatoryInputs = chosenFlow.getMandatoryInputs().size();
        optionalInputs = new ArrayList<>();
        initialVal = new HashMap<>();
        initialVal = chosenFlow.getInnitialDataValues();
        this.outputs = chosenFlow.getOutputs();
        int currentMandatoryInputs = 0;
        currentFilledMandatoryInputs = 0;
        int currentOptionalInputs = 0;
        for(InputWithStepName input : chosenFlow.getMandatoryInputs()){
            if(input.getDataDefinitionDeclaration().dataDefinition().getType()==Number.class) {
                if (initialVal.get(input.getDataDefinitionDeclaration().getName()) != null) {
                    int initialValInt = Integer.parseInt(initialVal.get(input.getDataDefinitionDeclaration().getName()));
                    Spinner textField = new Spinner(initialValInt, initialValInt, initialValInt);
                    textField.setEditable(false);
                    mandatoryInputs.add(textField);
                    currentFilledMandatoryInputs++;
                } else {
                    mandatoryInputs.add(new javafx.scene.control.Spinner<Integer>(0, 100, 0, 1));
                }
            }
            else if (input.getDataDefinitionDeclaration().dataDefinition().getType()==Enum.class) {
                //TODO: allow other enums that are not zip (check what is the type by step name)
                    if (initialVal.get(input.getDataDefinitionDeclaration().getName()) != null) {
                            TextField textField = new TextField(initialVal.get(input.getDataDefinitionDeclaration().getName()));
                            textField.setEditable(false);
                            mandatoryInputs.add(textField);
                            currentFilledMandatoryInputs++;
                        }
                     else {
                         mandatoryInputs.add(new javafx.scene.control.ChoiceBox<>(FXCollections.observableArrayList(ZipperEnumerator.values())));
                         }
            }
            else{
                if (initialVal.get(input.getDataDefinitionDeclaration().getName()) != null) {
                    TextField textField = new TextField(initialVal.get(input.getDataDefinitionDeclaration().getName()));
                    textField.setEditable(false);
                    mandatoryInputs.add(textField);
                    currentFilledMandatoryInputs++;
                }
                else
                    mandatoryInputs.add(new javafx.scene.control.TextField());
            }
            //add a listener to check if all mandatory inputs are filled

                if(mandatoryInputs.get(currentMandatoryInputs) instanceof javafx.scene.control.Spinner){
                ((javafx.scene.control.Spinner) mandatoryInputs.get(CurrenLisetenerIndex)).valueProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue != null){
                        currentFilledMandatoryInputs++;
                        if(currentFilledMandatoryInputs == currentAmountOfMandatoryInputs){
                            executeButton.setDisable(false);
                        }
                    }
                    else {
                        currentFilledMandatoryInputs--;
                        executeButton.setDisable(true);
                    }
                });
            }
            else if(mandatoryInputs.get(currentMandatoryInputs) instanceof javafx.scene.control.ChoiceBox){
                ((javafx.scene.control.ChoiceBox) mandatoryInputs.get(CurrenLisetenerIndex)).valueProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue != null){
                        currentFilledMandatoryInputs++;
                        if(currentFilledMandatoryInputs == currentAmountOfMandatoryInputs){
                            executeButton.setDisable(false);
                        }
                    }
                    else {
                        currentFilledMandatoryInputs--;
                        executeButton.setDisable(true);
                    }
                });
            }
            else{
                ((javafx.scene.control.TextField) mandatoryInputs.get(CurrenLisetenerIndex)).textProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue != null && !newValue.equals("")){
                        currentFilledMandatoryInputs++;
                        if(currentFilledMandatoryInputs == currentAmountOfMandatoryInputs){
                            executeButton.setDisable(false);
                        }
                    }
                    if(newValue == null || newValue.equals("")){
                        currentFilledMandatoryInputs--;
                        executeButton.setDisable(true);
                    }
                });
            }
            CurrenLisetenerIndex++;
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
    public synchronized void pupulateCurrentExecutionSteps(){
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
            executionData = new ArrayList<>();
            pupulateCurrentExecutionSteps();

        // Create a Task to execute the executeFlowUI() method
        Timer timer = new Timer();
        int interval = 100; // Interval in milliseconds
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                CountDownLatch latch = new CountDownLatch(1);
                mainController.getMenuVariables().executeFlow(chosenFlow, mandatoryInputs, optionalInputs, outputs, executionData, latch);
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    timer.cancel();
                }
//                FLowExecutor fLowExecutor = new FLowExecutor();
//                mainController.getMenuVariables().getStats().put(mainController.getMenuVariables().getUniqueFlowExecutionIdCounter(),
//                        fLowExecutor.executeFlowUI(mainController.getMenuVariables().getFlowExecutionMapFromFlowName().get(chosenFlow.getName()),
//                                mandatoryInputs, optionalInputs, outputs, executionData,
//                                mainController.getMenuVariables().getStepExecutionStatisticsMap()));
//                mainController.getMenuVariables().upuniqueFlowExecutionIdCounter();
//                if (mainController.getMenuVariables().getFlowExecutionsStatisticsMap().containsKey(chosenFlow.getName())) {
//                    mainController.getMenuVariables().getFlowExecutionsStatisticsMap().get(
//                            chosenFlow.getName()).addFlowExecutionStatistics(mainController.getMenuVariables().getStats().get(mainController.getMenuVariables().getUniqueFlowExecutionIdCounter() - 1));
//                } else {
//                    FlowExecutionsStatistics flowExecutionsStatistics = new FlowExecutionsStatistics(chosenFlow.getName());
//                    flowExecutionsStatistics.addFlowExecutionStatistics(mainController.getMenuVariables().getStats().get(mainController.getMenuVariables().getUniqueFlowExecutionIdCounter() - 1));
//                    mainController.getMenuVariables().getFlowExecutionsStatisticsMap().put(chosenFlow.getName(), flowExecutionsStatistics);
//
//                }
                return null;
            }
        };
        // Set up the completion handler when the task is finished
        task.setOnSucceeded(e -> {
            // This code will run on the JavaFX application thread
            //timer.cancel();
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

//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run()
//            {
//                // This code will run on the background thread
//                Platform.runLater(() -> {
//                    pupulateCurrentExecutionSteps();
//                });
//            }
//        }, interval, interval);
    }

    public void continuation(ActionEvent event)
    {
    }
}
