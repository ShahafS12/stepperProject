package mainSceneAdmin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;

import java.net.URL;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.InetAddress;
import java.io.IOException;

public class UIRunnerAdmin extends Application
{
    private static ServerSocket SERVER_SOCKET;

    public static void main(String[] args)
    {
        try {
            SERVER_SOCKET = new ServerSocket(9999, 0, InetAddress.getByAddress(new byte[] {127,0,0,1}));
        } catch (BindException e) {
            System.out.println("Application is already running.");
            System.exit(1);
        } catch (IOException e) {
            System.out.println("Unexpected error. Exiting.");
            System.exit(2);
        }

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
        javafx.scene.Scene scene = new javafx.scene.Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new javafx.scene.image.Image("mainSceneAdmin/imagez.png"));
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            mainController.close();
            try {
                if (SERVER_SOCKET != null) {
                    SERVER_SOCKET.close();
                }
            } catch (IOException e) {
                System.out.println("Failed to close socket. Exiting.");
                System.exit(3);
            }
        });
    }
}
