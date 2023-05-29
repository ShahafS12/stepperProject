package historyScene;

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
import javafx.scene.text.TextFlow;
import mainScene.mainController;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.step.api.StepExecutionStatistics;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;
import javafx.scene.control.TableColumn;


import java.util.Map;

public class historySceneController {

    private mainController mainController;

    public void initialize() {
        if (mainController != null) {
            mainController.setHistoryController(this);
        }
    }

    @FXML
    private AnchorPane historyAnchorPane;

    @FXML
    private TextFlow detailsPerExecution;

    @FXML
    private ListView<?> oldFlowsListView;

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

    }

    public AnchorPane getHistoryAnchorPane() {
        return historyAnchorPane;
    }

    public void setMainController(mainController mainController) {
        this.mainController = mainController;
    }

    public void setTableInHistory() {
        Map<Integer, FlowExecutionStatistics> flowExecutionsStatisticsMap = mainController.getMenuVariables().getStats();
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
}