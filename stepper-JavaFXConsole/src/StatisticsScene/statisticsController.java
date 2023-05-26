package StatisticsScene;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import mainScene.mainController;
import mta.course.java.stepper.flow.definition.api.FlowExecutionStatistics;
import mta.course.java.stepper.stepper.FlowExecutionsStatistics;

public class statisticsController {
    @FXML
    private AnchorPane statisticsAnchorPane;
    @FXML
    private TableView<?> flowsStatiticsTable;

    @FXML
    private TableColumn<?, ?> FlowNameCol;

    @FXML
    private TableColumn<?, ?> FlowExecutionsCol;

    @FXML
    private TableColumn<?, ?> flowAvgDurationCol;

    @FXML
    private TableView<?> StepStatisticsTable;

    @FXML
    private TableColumn<?, ?> stepNameCol;

    @FXML
    private TableColumn<?, ?> StepExecutionCounterCol;

    @FXML
    private TableColumn<?, ?> StepAvgDurationCol;
    private mainScene.mainController mainController;
    private FlowExecutionsStatistics flowExecutionsStatistics;
    private FlowExecutionStatistics flowExecutionStatistics;
    public  void setMainController(mainController mainController) {
        this.mainController = mainController;
    }
    @FXML
    public void initialize() {
        if (mainController != null) {
            mainController.setStatisticsController(this);
        }
    }
    public AnchorPane getStatisticsAnchorPane(){
        return statisticsAnchorPane;
    }
    public void setFlowExecutionsStatistics(FlowExecutionsStatistics flowExecutionsStatistics) {
        this.flowExecutionsStatistics = flowExecutionsStatistics;
    }
    public void setFlowExecutionStatistics(FlowExecutionStatistics flowExecutionStatistics) {
        this.flowExecutionStatistics = flowExecutionStatistics;
    }

}

