package mainScene;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import mainScene.mainController;
import topScene.topController;

import java.net.URL;

public class UIRunner extends Application
{
    public static void main(String[] args)
    {
        Application.launch(args);
    }

    @Override
    public void start(javafx.stage.Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Stepper");
        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("mainScene.fxml");
        fxmlLoader.setLocation(url);
        javafx.scene.Parent root = fxmlLoader.load();
        mainController mainController = fxmlLoader.getController();
        //topController.setModel(model);
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 300, 275);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
