package StatisticsScene;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mainScene.mainController;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.step.api.StepExecutionStatistics;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;

import java.util.List;
import java.util.Map;

public class statisticsController {
    @FXML
    private AnchorPane statisticsAnchorPane;
    @FXML
    private TableView<FlowExecutionsStatistics> flowsStatiticsTable;

    @FXML
    private TableColumn<String, String> FlowNameCol;

    @FXML
    private TableColumn<String, Integer> FlowExecutionsCol;

    @FXML
    private TableColumn<String, Double> flowAvgDurationCol;

    @FXML
    private TableView<StepExecutionStatistics> StepStatisticsTable;

    @FXML
    private TableColumn<String, String> stepNameCol;

    @FXML
    private TableColumn<String, Integer> StepExecutionCounterCol;

    @FXML
    private TableColumn<String, Double> StepAvgDurationCol;

    @FXML
    private Button showChartButton;

    @FXML
    private Button showChartButtonSteps;


    private mainScene.mainController mainController;
    private Map<String, FlowExecutionsStatistics> flowExecutionsStatisticsMap;
    private FlowExecutionStatistics flowExecutionStatistics;
    public  void setMainController(mainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void initialize() {
        if (mainController != null) {
            mainController.setStatisticsController(this);
        }

        showChartButton.setOnAction(e -> showBarChart());
        showChartButtonSteps.setOnAction(e -> showBarChartSteps());
    }
    public AnchorPane getStatisticsAnchorPane(){
        return statisticsAnchorPane;
    }
    public void setFlowExecutionsStatistics(Map<String, FlowExecutionsStatistics> flowExecutionsStatisticsList) {
        this.flowExecutionsStatisticsMap = flowExecutionsStatisticsList;
    }
    public void setFlowExecutionStatistics(FlowExecutionStatistics flowExecutionStatistics) {
        this.flowExecutionStatistics = flowExecutionStatistics;
    }
    public void populateFlowStatisticsTable(){
        // Create an ObservableList to hold the data
        ObservableMap<String, FlowExecutionsStatistics> observableMap = FXCollections.observableMap(flowExecutionsStatisticsMap);
        ObservableList<FlowExecutionsStatistics> rowData = FXCollections.observableArrayList();
        // Add a listener to the observableMap
        observableMap.addListener((MapChangeListener<String, FlowExecutionsStatistics>) change -> {
            if (change.wasAdded()) {
                rowData.add(change.getValueAdded());
            } else if (change.wasRemoved()) {
                rowData.remove(change.getValueRemoved());
            }
        });
        // Set the data to the table
        rowData.addAll(flowExecutionsStatisticsMap.values());
        flowsStatiticsTable.setItems(rowData);
        // Set the cell value factory to the table columns
        FlowNameCol.setCellValueFactory(new PropertyValueFactory<>("flowName"));
        FlowExecutionsCol.setCellValueFactory(new PropertyValueFactory<>("countHowManyTimesExecution"));
        flowAvgDurationCol.setCellValueFactory(new PropertyValueFactory<>("averageDuration"));

    }
    public void populateStepStatisticsTable(){
        // Create an ObservableList to hold the data
        ObservableMap<String, StepExecutionStatistics> observableMap = FXCollections.observableMap(mainController.getMenuVariables().getStepExecutionStatisticsMap());
        ObservableList<StepExecutionStatistics> rowData = FXCollections.observableArrayList();
        // Add a listener to the observableMap
        observableMap.addListener((MapChangeListener<String, StepExecutionStatistics>) change -> {
            if (change.wasAdded()) {
                rowData.add(change.getValueAdded());
            } else if (change.wasRemoved()) {
                rowData.remove(change.getValueRemoved());
            }
        });
        // Set the data to the table
        rowData.addAll(mainController.getMenuVariables().getStepExecutionStatisticsMap().values());
        StepStatisticsTable.setItems(rowData);
        // Set the cell value factory to the table columns
        StepExecutionCounterCol.setCellValueFactory(new PropertyValueFactory<>("countHowManyTimesExecution"));
        StepAvgDurationCol.setCellValueFactory(new PropertyValueFactory<>("averageDuration"));
        stepNameCol.setCellValueFactory(new PropertyValueFactory<>("stepName"));
    }
    public TableView<FlowExecutionsStatistics> getFlowsStatiticsTable(){
        return flowsStatiticsTable;
    }

    public BarChart<String, Number> tableViewInGraph (){

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (FlowExecutionsStatistics data : flowsStatiticsTable.getItems()) {
            series.getData().add(new XYChart.Data<>(data.getFlowName(), data.getCountHowManyTimesExecution()));
        }

        barChart.getData().add(series);

        return barChart;
    }

    private void showBarChart() {
        Stage stage = new Stage();
        Scene scene = new Scene(tableViewInGraph(), 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    public BarChart<String, Number> tableViewInGraphSteps (){

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (StepExecutionStatistics data : StepStatisticsTable.getItems()) {
            series.getData().add(new XYChart.Data<>(data.getStepName(), data.getCountHowManyTimesExecution()));
        }

        barChart.getData().add(series);

        return barChart;
    }

    private void showBarChartSteps() {
        Stage stage = new Stage();
        Scene scene = new Scene(tableViewInGraphSteps(), 400, 400);
        stage.setScene(scene);
        stage.show();
    }


}

