package showFlowScene;

import adapters.*;
import api.HttpStatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import mainSceneAdmin.mainAdminController;
import mta.course.java.stepper.flow.InputWithStepName;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import static util.Constants.GET_FLOW_DEFINITION;
import static util.Constants.REFRESH_RATE;

public class ShowFlowController {
    private mainAdminController mainController;
    private Timer timer;
    private TimerTask listRefresher;
    private mainSceneClient.mainClientController mainClientController;
    private final BooleanProperty autoUpdate;
    @FXML
    private AnchorPane showFlowAnchorPane;

    @FXML
    private ListView<String> flowsList;
    @FXML
    private Button executeButton;
    private String lastSelectedFlow;

    @FXML
    private VBox chosenFlowData;
    private Parent root;
    private Scene scene;
    private Stage stage;
    private HttpStatusUpdate httpStatusUpdate;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    public ShowFlowController()
    {
        autoUpdate = new SimpleBooleanProperty();
    }
    public BooleanProperty autoUpdatesProperty() {
        return autoUpdate;
    }

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
            if(mainController!=null) {
                FlowDefinition flow = mainController.getFlowDefinition(chosenFlow);
                mainController.switchToExecutionScene(event, flow);
            }
            else{
                FlowDefinition flow = mainClientController.getFlowDefinition(chosenFlow);
                mainClientController.switchToExecutionScene(event, flow);
            }
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
    public void setMainController(mainAdminController mainController) {
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
        if(lastSelectedFlow!=null){
            flowsList.getSelectionModel().select(lastSelectedFlow);
        }
    }
    public void setFlowsList(List<String> flowsNames,String selectedFlow) {
        flowsList.getItems().clear();
        flowsList.getItems().addAll(flowsNames);
        flowsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleFlowSelection(newValue);
            }
        });
        if(selectedFlow!=null){
            flowsList.getSelectionModel().select(selectedFlow);
        }
    }
    public void switchToStatisticsScene(){

    }

    private void handleFlowSelection(String newValue)
    {
        lastSelectedFlow = newValue;
        String finalUrl = HttpUrl.parse(GET_FLOW_DEFINITION)
                .newBuilder()
                .addQueryParameter("flowName", newValue)
                .build()
                .toString();
        HttpClientUtil.runAsync(finalUrl, new Callback(){
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
                    Platform.runLater(() -> {
                        System.out.println("Got flow definition");//todo should this be a log?
                        try {
                            String flowDefinitionFromServer = response.body().string();
                            Gson gson = new GsonBuilder()
                                    .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                                    .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                                    .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                                    .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                                    .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
                                    .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                                    .create();
                            FlowDefinitionImpl flow = gson.fromJson(flowDefinitionFromServer, FlowDefinitionImpl.class);
                            setChosenFlowData(flow);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }

        }
    });
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
    public void setHttpStatusUpdate(HttpStatusUpdate httpStatusUpdate) {
        this.httpStatusUpdate = httpStatusUpdate;
    }
    public void startListRefresher() {
        listRefresher = new FlowListRefresher(
                autoUpdate,
                httpStatusUpdate::updateHttpLine,
                this::setFlowsList);
        timer = new Timer();
        timer.schedule(listRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void setMainSceneClientController(mainSceneClient.mainClientController mainSceneClientController)
    {
        this.mainClientController = mainSceneClientController;
    }
}