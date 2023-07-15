package historyScene;

import adapters.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import mainSceneAdmin.mainAdminController;
import mainSceneClient.mainClientController;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.flow.execution.context.StepExecutionContext;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.SingleStepExecutionData;
import mta.course.java.stepper.step.api.StepExecutionStatistics;
import javafx.scene.control.TableColumn;
import okhttp3.HttpUrl;
import okhttp3.Response;
import util.http.HttpClientUtil;


import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static util.Constants.GET_FLOWS_ADMIN_HISTORY;

public class historySceneController {

    private mainAdminController mainController;
    private mainClientController mainControllerClient;
    private FlowExecutionStatistics currentFlow;


    public void initialize() {
        if (mainController != null) {
            mainController.setHistoryController(this);
        }
        tableHistory.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                FlowExecutionStatistics selectedExecution = tableHistory.getSelectionModel().getSelectedItem();
                if (selectedExecution != null) {
                    currentFlow = selectedExecution;
                    stepDetails.getChildren().clear();
                    displayExecutionDetails(selectedExecution);
                }
            }
        });

        stepsHistoryInFlow.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                String selectedStep = stepsHistoryInFlow.getSelectionModel().getSelectedItem();
                if (selectedStep != null)
                {
                    currentFlow.getSingleStepExecutionDataList().forEach(singleStepExecutionData -> {
                        if(singleStepExecutionData.getStepName().equals(selectedStep))
                            displayStepDetails(singleStepExecutionData);
                    });
                }
            }
        });
        // TODO: use shahaf function to display them after clicking on the return flow button.

//        returnFlowButton.setOnMouseClicked(event -> {
//            mainController.switchToExecutionScene(); //
//        });
    }


    @FXML
    private AnchorPane historyAnchorPane;

    @FXML
    private TextFlow stepDetails;

    @FXML
    private ListView<String> stepsHistoryInFlow;

    @FXML
    private Button returnFlowButton;

    @FXML
    private TableColumn<?, ?> flowNameCol;

    @FXML
    private TableColumn<?, ?> dateCol;

    @FXML
    private TableColumn<?, ?> resultCol;

    @FXML
    private TableView<FlowExecutionStatistics> tableHistory;

    @FXML
    void returnFlowAgain(ActionEvent event) {
        if(currentFlow!=null) {
            Map<String, Object> userInputsMap = currentFlow.getUserInputsMap();
            mainController.setReRunFlow(event, currentFlow.getFlow(), userInputsMap);
        }
    }

    public AnchorPane getHistoryAnchorPane() {
        return historyAnchorPane;
    }

    public void setMainController(mainAdminController mainController) {
        this.mainController = mainController;
    }

    public void setTableInHistory() {
        Map<Integer, FlowExecutionStatistics> flowExecutionsStatisticsMap = Collections.synchronizedMap(new HashMap<Integer, FlowExecutionStatistics>());;
        String finalUrl = HttpUrl.parse(GET_FLOWS_ADMIN_HISTORY)
                .newBuilder()
                .build()
                .toString();

        Response response = HttpClientUtil.run(finalUrl);
        if (response.isSuccessful()) {
                String responseString = null;
                try {
                    responseString = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                    .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                    .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                    .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                    .registerTypeAdapter(FlowDefinition.class, new FlowDefinitionAdapter())
                    .registerTypeAdapter(StepExecutionContext.class, new StepExecutionContextAdapter())
                    .create();

            Type type = new TypeToken<Map<Integer, FlowExecutionStatistics>>(){}.getType();
            Map<Integer, FlowExecutionStatistics> map = gson.fromJson(responseString, type);
            if(map!=null)
                flowExecutionsStatisticsMap.putAll(map);
        }

        //Map<Integer, FlowExecutionStatistics> flowExecutionsStatisticsMap = mainController.getMenuVariables().getStats(); // TODO: get stats from flow manager
        ObservableMap<Integer, FlowExecutionStatistics> observableMap = FXCollections.observableMap(flowExecutionsStatisticsMap);
        ObservableList<FlowExecutionStatistics> rowData = FXCollections.observableArrayList();

        observableMap.addListener((MapChangeListener<Integer, FlowExecutionStatistics>) change -> {
            if (change.wasAdded()) {
                rowData.add(change.getValueAdded());
            } else if (change.wasRemoved()) {
                rowData.remove(change.getValueRemoved());
            }
        });

        rowData.addAll(flowExecutionsStatisticsMap.values());
        tableHistory.setItems(rowData);
        flowNameCol.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("dateTime"));
        resultCol.setCellValueFactory(new PropertyValueFactory<>("flowResult"));
    }


    private void displayStepDetails(SingleStepExecutionData newValue){
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

    private void displayExecutionDetails(FlowExecutionStatistics selectedExecution) {
        stepsHistoryInFlow.getItems().clear();
        Map<String,StepExecutionStatistics> steps = selectedExecution.getStepExecutionStatisticsMap();
        for (String key : steps.keySet()){
            stepsHistoryInFlow.getItems().add(key);
        }
    }

}