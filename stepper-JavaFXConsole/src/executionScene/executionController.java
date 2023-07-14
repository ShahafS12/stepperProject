package executionScene;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
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
import mainSceneAdmin.mainAdminController;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.Continuation;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.flow.execution.FlowExecutionResult;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepDefinition;
import mta.course.java.stepper.step.impl.ZipperStep.ZipperEnumerator;
import netscape.javascript.JSObject;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.*;

import java.util.concurrent.*;

import static util.Constants.EXECUTE_FLOW;
import static util.Constants.GET_FLOW_ID_UNIQUE_EXECUTION_ID;


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
    private mainAdminController mainController;
    private FlowDefinition chosenFlow;
    private List<SingleStepExecutionData> executionData;
    private Map<String, String> initialVal;
    private int currentFilledMandatoryInputs;
    private ScheduledFuture<?> currentExecutionUpdater;
    private ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
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
    public void setMainController(mainAdminController mainController){
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
        if(chosenFlow==null)
            return;
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
        stepDetails.getChildren().clear();
        final int[] uniqueExecutionId = new int[1];
        String uniqueidURL = HttpUrl.parse(GET_FLOW_ID_UNIQUE_EXECUTION_ID)
                .newBuilder()
                .build()
                .toString();
        HttpClientUtil.runAsync(uniqueidURL, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                System.out.println("Failed to get flow definition");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)//todo check how to print the error message to screen
                    );
                }
                else {
                    String responseBody = response.body().string();
                    Gson gson = new Gson();
                    uniqueExecutionId[0] = gson.fromJson(responseBody, Integer.class);
                }
            }
        });
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
                List<Object> mandatoryInputsObject = new ArrayList<>();
                List<Object> optionalInputsObject = new ArrayList<>();
                for (Control control : mandatoryInputs) {
                    mandatoryInputsObject.add(getControlContent(control));
                }
                for (Control control : optionalInputs) {
                    optionalInputsObject.add(getControlContent(control));
                }
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                        .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                        .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                        .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                        .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                        .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
                        .create();
                String mandatoryInputsObjectString = gson.toJson(mandatoryInputsObject);
                String optionalInputsObjectString = gson.toJson(optionalInputsObject);
                String outputsJson = gson.toJson(chosenFlow.getOutputs());
                String executionDataJson = gson.toJson(executionData);
                String[] jsonArray = {chosenFlow.getName(),mandatoryInputsObjectString, optionalInputsObjectString,
                        outputsJson, executionDataJson};
                String jsonString = gson.toJson(jsonArray);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonString);
                Request request = new Request.Builder()
                        .url(EXECUTE_FLOW)
                        .post(body)
                        .build();
                Response response = HttpClientUtil.run(request);
                //mainController.getMenuVariables().executeFlow(chosenFlow, mandatoryInputsObject, optionalInputsObject, outputs, executionData, latch);
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
    public void shutdownExecutorService() {
        executorService.shutdown();
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
    private Object getControlContent(Control c){
        if(c instanceof TextField){
            return ((TextField) c).getText();
        }
        else if (c instanceof Spinner<?>){
            return ((Spinner<?>) c).getValue();
        }
        else if(c instanceof ChoiceBox<?>){
            return ((ChoiceBox<?>) c).getValue().toString();
        }
        return  null;
    }
}
