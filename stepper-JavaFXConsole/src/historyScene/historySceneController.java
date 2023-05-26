package historyScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.TextFlow;
import mainScene.mainController;

public class historySceneController {

    private mainController mainController;

    public void initialize(){

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
    private TableView<?> tableHistory;

    @FXML
    void returnFlowAgain(ActionEvent event) {

    }

    public AnchorPane getHistoryAnchorPane() {
        return historyAnchorPane;
    }

    public void setMainController(mainController mainController) {
        this.mainController = mainController;
    }



}
