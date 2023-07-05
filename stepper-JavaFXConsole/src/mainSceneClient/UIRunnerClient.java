package mainSceneClient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import loginScene.loginController;

import java.net.URL;

public class UIRunnerClient extends Application
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
        URL url = getClass().getResource("/loginScene/login.fxml");
        fxmlLoader.setLocation(url);
        javafx.scene.Parent root = fxmlLoader.load();
        loginController loginController = fxmlLoader.getController();
        loginController.setStage(primaryStage);
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new javafx.scene.image.Image("mainSceneAdmin/imagez.png"));
        primaryStage.show();
    }
}
