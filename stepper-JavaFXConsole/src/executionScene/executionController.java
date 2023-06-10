package executionScene;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.util.Duration;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.Continuation;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.execution.FlowExecutionResult;
import mta.course.java.stepper.flow.execution.runner.FLowExecutor;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.impl.ZipperStep.ZipperEnumerator;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;

import java.util.*;

import java.util.concurrent.*;


public class executionController {
    @FXML
    private AnchorPane executionAnchorPane;

    @FXML
    private GridPane inputsGridPane;

    @FXML
    private VBox continuationVbox;

    @FXML
    private Button executeButton;

    @FXML
    private ListView<String> currentExecutionSteps;

    @FXML
    private Button flowContinuationButton;
    @FXML
    private TextFlow stepDetails;
    private List<Control> mandatoryInputs;
    private Map<String,Object> continuationMap;
    private List<Control> optionalInputs;
    private List<InputWithStepName> outputs;
    private mainScene.mainController mainController;
    private FlowDefinition chosenFlow;
    private List<SingleStepExecutionData> executionData;
    private Map<String, String> initialVal;
    private int currentFilledMandatoryInputs;
    private ScheduledFuture<?> currentExecutionUpdater;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
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
        currentExecutionUpdater = null;
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
        refresh();
        this.chosenFlow = chosenFlow;
        continuationMap = new HashMap<>();//to not crash when searching for continuation key
        populateInputsGridPane();
    }
    public void setChosenFlow(FlowDefinition chosenFlow, Map<String,List<String>> continuationMap){
        refresh();
        this.chosenFlow = chosenFlow;
        fillContinuationMap(continuationMap);
        populateInputsGridPane();
        if(currentFilledMandatoryInputs == currentAmountOfMandatoryInputs){
            executeButton.setDisable(false);
        }
    }
    public void setReRunFlow(FlowDefinition chosenFlow,Map<String, Object> userInputsMap){
        refresh();
        this.chosenFlow = chosenFlow;
        this.continuationMap = userInputsMap;
        populateInputsGridPane();
        executeButton.setDisable(false);
    }
    public void refresh(){//clears all the data from the previous flow in ui
        inputsGridPane.getChildren().clear();
        continuationVbox.getChildren().clear();
        executeButton.setDisable(true);
        currentExecutionSteps.getItems().clear();
        stepDetails.getChildren().clear();
    }
    private void populateInputsGridPane(){
        inputsGridPane.getChildren().clear();
        continuationVbox.getChildren().clear();
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
            String inputWithStepName = chosenFlow.getAllFlowLevelAlias().get(input.getStepName() + "." + input.getDataDefinitionDeclaration().getName());
            if(inputWithStepName == null)//if no alias
                inputWithStepName = input.getStepName() + "." + input.getDataDefinitionDeclaration().getName();
            //NUMBER
            if(input.getDataDefinitionDeclaration().dataDefinition().getType()==Number.class) {
                currentAmountOfMandatoryInputs--;//a number is a spinner so it is always filled
                if (initialVal.get(input.getDataDefinitionDeclaration().getName()) != null) {
                    int initialValInt = Integer.parseInt(initialVal.get(inputWithStepName));
                    Spinner textField = new Spinner(initialValInt, initialValInt, initialValInt);
                    textField.setEditable(false);
                    mandatoryInputs.add(textField);
                    //currentFilledMandatoryInputs++;
                }
                else if(continuationMap.containsKey(inputWithStepName)){
                    int initialValInt = Integer.parseInt(continuationMap.get(inputWithStepName).toString());
                    Spinner textField = new Spinner(0, 100,initialValInt ,1);
                    textField.setEditable(true);
                    mandatoryInputs.add(textField);
                    //currentFilledMandatoryInputs++;
                }
                else {
                    mandatoryInputs.add(new javafx.scene.control.Spinner<Integer>(0, 100, 0, 1));
                    //currentFilledMandatoryInputs++;
                }
            }
            //ENUM
            else if (input.getDataDefinitionDeclaration().dataDefinition().getType()==Enum.class) {
                //TODO: allow other enums that are not zip (check what is the type by step name)
                if (initialVal.get(input.getDataDefinitionDeclaration().getName()) != null) {
                    TextField textField = new TextField(initialVal.get(input.getDataDefinitionDeclaration().getName()));
                    textField.setEditable(false);
                    mandatoryInputs.add(textField);
                    currentFilledMandatoryInputs++;
                }
                else if(continuationMap.containsKey(inputWithStepName)){
                    ChoiceBox choiceBox = new ChoiceBox(FXCollections.observableArrayList(ZipperEnumerator.values()));
                    choiceBox.getSelectionModel().select(ZipperEnumerator.valueOf(continuationMap.get(inputWithStepName).toString()));
                    mandatoryInputs.add(choiceBox);
                    currentFilledMandatoryInputs++;
                }
                else {
                    mandatoryInputs.add(new javafx.scene.control.ChoiceBox<>(FXCollections.observableArrayList(ZipperEnumerator.values())));
                }
            }
            //TEXT
            else{
                if (initialVal.get(input.getDataDefinitionDeclaration().getName()) != null) {
                    TextField textField = new TextField(initialVal.get(input.getDataDefinitionDeclaration().getName()));
                    textField.setEditable(false);
                    mandatoryInputs.add(textField);
                    currentFilledMandatoryInputs++;
                }
                else if(continuationMap.containsKey(inputWithStepName)){
                    TextField textField = new TextField(continuationMap.get(inputWithStepName).toString());
                    textField.setEditable(true);
                    mandatoryInputs.add(textField);
                    currentFilledMandatoryInputs++;
                }
                else
                    mandatoryInputs.add(new javafx.scene.control.TextField());
            }
            //add a listener to check if all mandatory inputs are filled

                if(mandatoryInputs.get(CurrenLisetenerIndex) instanceof javafx.scene.control.Spinner){
//                ((javafx.scene.control.Spinner) mandatoryInputs.get(CurrenLisetenerIndex)).valueProperty().addListener((observable, oldValue, newValue) -> {
//                    if(newValue != null){
//                        currentFilledMandatoryInputs++;
//                        if(currentFilledMandatoryInputs == currentAmountOfMandatoryInputs){
//                            executeButton.setDisable(false);
//                        }
//                    }
//                    else {
//                        currentFilledMandatoryInputs--;
//                        executeButton.setDisable(true);
//                    }
//                });
            }
            else if(mandatoryInputs.get(CurrenLisetenerIndex) instanceof javafx.scene.control.ChoiceBox){
                ((javafx.scene.control.ChoiceBox) mandatoryInputs.get(CurrenLisetenerIndex)).valueProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue != null && oldValue == null){
                        currentFilledMandatoryInputs++;
                        if(currentFilledMandatoryInputs == currentAmountOfMandatoryInputs){
                            executeButton.setDisable(false);
                        }
                    }
                    else {//can never be unfilled once filled
                        //currentFilledMandatoryInputs--;
                        //executeButton.setDisable(true);
                    }
                });
            }
            else{
                ((javafx.scene.control.TextField) mandatoryInputs.get(CurrenLisetenerIndex)).textProperty().addListener((observable, oldValue, newValue) -> {
                    if(newValue != null && !newValue.equals("") && oldValue.equals("")){
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
            String inputWithStepName = chosenFlow.getAllFlowLevelAlias().get(input.getStepName() + "." + input.getDataDefinitionDeclaration().getName());
            if(inputWithStepName == null)//if no alias
                inputWithStepName = input.getStepName() + "." + input.getDataDefinitionDeclaration().getName();
            //NUMBER
            if(input.getDataDefinitionDeclaration().dataDefinition().getType()==Integer.class) {
                if(continuationMap.containsKey(inputWithStepName)){
                    int initialValInt = Integer.parseInt(continuationMap.get(inputWithStepName).toString());
                    Spinner textField = new Spinner(0, 100,initialValInt ,1);
                    textField.setEditable(true);
                    optionalInputs.add(textField);
                } else {
                    optionalInputs.add(new javafx.scene.control.Spinner<Integer>(0, 100, 0, 1));
                }
            }
            else {//TEXT
                if(continuationMap.containsKey(inputWithStepName)){
                    TextField textField = new TextField(continuationMap.get(inputWithStepName).toString());
                    textField.setEditable(true);
                    optionalInputs.add(textField);
                }
                else
                    optionalInputs.add(new javafx.scene.control.TextField());
            }

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
        continuationVbox.getChildren().clear();
        int id = mainController.getMenuVariables().getUniqueFlowExecutionIdCounter();
        // Create a Task to execute the executeFlowUI() method
        Timer timer = new Timer();
        int interval = 100; // Interval in milliseconds
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                CountDownLatch latch = new CountDownLatch(1);
                if (currentExecutionUpdater != null && !currentExecutionUpdater.isDone()) {
                    currentExecutionUpdater.cancel(false);
                }
                mainController.getMenuVariables().executeFlow(chosenFlow, mandatoryInputs, optionalInputs, outputs, executionData, latch);
                try {
                    currentExecutionUpdater = executorService.scheduleAtFixedRate(() -> Platform.runLater(() -> {
                        pupulateCurrentExecutionSteps();
                    }), 0, interval, TimeUnit.MILLISECONDS);
                    latch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally{
                    timer.cancel();
                }
                return null;
            }
        };
        // Set up the completion handler when the task is finished
        task.setOnSucceeded(e -> {
            // This code will run on the JavaFX application thread
            //timer.cancel();
            currentExecutionUpdater.cancel(false);
            pupulateCurrentExecutionSteps();
            populateContinuation();
            mainController.refreshStatisticsScene();
//            if(mainController.populateStepStatisticsTable().)
//            mainController.populateStepStatisticsTable();
            //show gif according to result
            if(mainController.getMenuVariables().getStats().get(id).getFlowResult()== FlowExecutionResult.SUCCESS){
                mainController.showGifForDuration("mainScene/giphy.gif", new Duration(2000));
            }
            else if(mainController.getMenuVariables().getStats().get(id).getFlowResult()== FlowExecutionResult.FAILURE){
                mainController.showGifForDuration("mainScene/complete-failure-failure.gif", new Duration(2000));
            }
            else{//warning
                mainController.showGifForDuration("mainScene/error-img.gif", new Duration(2000));
                //TODO: add warning gif
            }
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

    private void populateContinuation()
    {
        continuationVbox.getChildren().clear();
        Label continuationLabel = new Label("Continuations:");
        continuationLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16");
        continuationVbox.getChildren().add(continuationLabel);
        Button[] buttons = new Button[chosenFlow.getContinuations().size()];
        int i = 0;
        for(Continuation continuation : chosenFlow.getContinuations())
        {
            buttons[i] = new Button(continuation.getTargetFlow());
            buttons[i].setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    FlowDefinition flow = mainController.getFlowDefinition(continuation.getTargetFlow());
                    mainController.switchToExecutionSceneWithContinuation(event, flow, continuation.getContinuationMapping());
                }
            });
            continuationVbox.getChildren().add(buttons[i]);
            i++;
        }
    }
    public void fillContinuationMap(Map<String, List<String>> continuationMapping){//todo: this should be done in the engine
        this.continuationMap = new HashMap<>();
        Map<String,Object> inputs = mainController.getMenuVariables().getCurrentStatsFlowExecuted().getUserInputsMap();
        for(String key : inputs.keySet()){
            this.continuationMap.put(key, inputs.get(key));
        }
        //add the continuation mapping
        for(String key : continuationMapping.keySet()){
            List<String> values = continuationMapping.get(key);
            for(String value : values){
                if (inputs.get(key)!=null)
                    this.continuationMap.put(value, inputs.get(key));
            }
        }
    }
}
