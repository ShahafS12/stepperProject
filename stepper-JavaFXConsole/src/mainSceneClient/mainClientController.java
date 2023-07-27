package mainSceneClient;

import adapters.*;
import api.HttpStatusUpdate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import executionScene.executionController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import mta.course.java.stepper.flow.definition.api.FlowDefinition;
import mta.course.java.stepper.flow.definition.api.FlowDefinitionImpl;
import mta.course.java.stepper.flow.definition.api.StepUsageDeclaration;
import mta.course.java.stepper.menu.MenuVariables;
import mta.course.java.stepper.step.api.DataDefinitionDeclaration;
import mta.course.java.stepper.step.api.StepDefinition;
import okhttp3.HttpUrl;
import okhttp3.Response;
import showFlowScene.ShowFlowController;
import topClientScene.topClientController;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static util.Constants.*;

public class mainClientController implements HttpStatusUpdate
{
    @FXML private topClientController topClientComponentController;

    @FXML private ShowFlowController ShowFlowComponentController;

    @FXML private BorderPane mainBorder;
    private executionScene.executionController executionController;
    private MenuVariables menuVariables;
    private historyScene.historySceneController historySceneController;
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    @FXML
    public void initialize()
    {
        topClientComponentController.setIsManager(false); // default NO manager
        if(topClientComponentController != null && ShowFlowComponentController != null)
        {
            topClientComponentController.setMainSceneClientController(this);
            ShowFlowComponentController.setMainSceneClientController(this);
            ShowFlowComponentController.setHttpStatusUpdate(this);
        }
        setActive();
    }
    public void updateUserName(String userName)
    {
        topClientComponentController.setClientName(userName);
    }
    public String getUserName()
    {
        return topClientComponentController.getClientName();
    }
    public void switchToExecutionScene(ActionEvent event){
        if(executionController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/executionScene/executionScene.fxml"));
            try {
                Parent executionRoot = loader.load();
                executionController = loader.getController();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void switchToExecutionScene(ActionEvent event, FlowDefinition chosenFlow){
        if(executionController == null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/executionScene/executionScene.fxml"));
            try {
                Parent executionRoot = loader.load();
                executionController = loader.getController();
                executionController.setMainController(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            executionController.setChosenFlow(chosenFlow);
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void switchToHistoryScene(ActionEvent event){

        String finalUrl = HttpUrl.parse(GET_FLOWS_ADMIN_HISTORY)
                .newBuilder()
                .build()
                .toString();

        Response response = HttpClientUtil.run(finalUrl);

        if (response.code() == 200) {

            if (historySceneController == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(HISTORY_PAGE_FXML_RESOURCE_LOCATION));
                try {
                    Parent executionRoot = loader.load();
                    historySceneController = loader.getController();
                    historySceneController.setMainControllerClient(this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                AnchorPane view = historySceneController.getHistoryAnchorPane();
                mainBorder.setCenter(view);
                historySceneController.setTableInHistoryClient(this.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("No flows available!");
            alert.showAndWait();
        }
        //        if(menuVariables == null) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error Dialog");
//            alert.setHeaderText(null);
//            alert.setContentText("Please load a stepper first!");
//            alert.showAndWait();
//        }
//        else {
//            if (historySceneController == null) {
//                FXMLLoader loader = new FXMLLoader(getClass().getResource("/historyScene/historySceneBuilder.fxml"));
//                try {
//                    Parent executionRoot = loader.load();
//                    historySceneController = loader.getController();
//                    //historySceneController.setMainController(this);
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            try {
//                AnchorPane view = historySceneController.getHistoryAnchorPane();
//                mainBorder.setCenter(view);
//                historySceneController.setTableInHistory();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
    public void switchToExecutionSceneWithContinuation(ActionEvent event, FlowDefinition chosenFlow, Map<String, List<String>> continuation){
        //don't need to check if executionController is null because it is not null when we get here
        try {
            executionController.setChosenFlow(chosenFlow, continuation);
            AnchorPane view = executionController.getExecutionAnchorPane();
            mainBorder.setCenter(view);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void refreshExecutionScene(){
        if(executionController != null) {
            executionController.refresh();
        }
    }
    public void setExecutionController(executionController executionController)
    {
        this.executionController = executionController;
    }
    public void switchToShowFlowScene(ActionEvent event){
        try {
            AnchorPane view = ShowFlowComponentController.getShowFlowAnchorPane();
            mainBorder.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public FlowDefinition getFlowDefinition(String newValue)
    {
        String finalUrl = HttpUrl.parse(GET_FLOW_DEFINITION)
                .newBuilder()
                .addQueryParameter("flowName", newValue)
                .build()
                .toString();
        updateHttpLine("Getting flow definition from server for" + newValue);
        Response response = HttpClientUtil.run(finalUrl);
        try {
            if(response.code() != 200){
                Platform.runLater(() -> {
                    try {
                        errorMessageProperty.set("Something went wrong: " + response.body().string());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                return null;
            } else {
                System.out.println("Got flow definition");
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(Class.class, new ClassTypeAdapter())
                        .registerTypeAdapter(DataDefinitionDeclaration.class, new DataDefinitionDeclarationAdapter())
                        .registerTypeAdapter(StepUsageDeclaration.class, new StepUsageDeclarationAdapter())
                        .registerTypeAdapterFactory(new ClassTypeAdapterFactory())
                        .registerTypeAdapter(DataDefinitionAdapter.class, new DataDefinitionAdapter())
                        .registerTypeAdapter(StepDefinition.class, new StepDefinitionAdapter())
                        .create();
                return gson.fromJson(response.body().string(), FlowDefinitionImpl.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public MenuVariables getMenuVariables()
    {
        return menuVariables;
    }

    public void setMenuVariables(MenuVariables variables){
        this.menuVariables = variables;
    }
    public void setShowFlowComponentController(){
        ShowFlowComponentController.setFlowsList(menuVariables.getFlowNames());
    }
    public void setActive(){
        ShowFlowComponentController.startListRefresher();
    }

    public void close()
    {
        if(executionController != null)
        {
            if(menuVariables != null)
                menuVariables.shutdownExecutorService();
            if(executionController != null)
                executionController.shutdownExecutorService();
        }
    }

    @Override
    public void updateHttpLine(String line)
    {
        System.out.println("updateHttpLine");
        //todo - check if this is actually needed
    }
}
