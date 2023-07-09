package mainSceneAdmin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import java.net.URL;

public class UIRunnerAdmin extends Application
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
        mainAdminController mainController = fxmlLoader.getController();
        //topController.setModel(model);
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 700, 600);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new javafx.scene.image.Image("mainSceneAdmin/imagez.png"));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            mainController.close();
        });
    }
}
