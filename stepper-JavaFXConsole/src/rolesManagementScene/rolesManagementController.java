package rolesManagementScene;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import mainSceneAdmin.mainAdminController;

public class rolesManagementController
{
    @FXML
    private AnchorPane userManagementAnchorPane;

    @FXML
    private Button newRoleButton;

    @FXML
    private ListView<?> availableUsersListView;

    @FXML
    private VBox chosenUserInfo;

    @FXML
    private Button SaveButton;
    private mainAdminController mainController;

    @FXML
    public void SaveRoles(ActionEvent event) {
        return;
    }

    @FXML
    public void createNewRole(ActionEvent event) {
        return;
    }

    public void setMainController(mainAdminController mainAdminController)
    {
        this.mainController = mainAdminController;
    }

    public AnchorPane getRolesManagementAnchorPane()
    {
        return userManagementAnchorPane;
    }
}
