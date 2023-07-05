package loginScene;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import mainSceneAdmin.mainController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;

public class loginController {
    private Stage stage;
    @FXML
    private BorderPane loginBorderPane;

    @FXML
    private Label welcomeLabel;

    @FXML
    private HBox midSectionHbox;

    @FXML
    private Label userNameLabel;

    @FXML
    private TextField userNameTextField;

    @FXML
    private Button loginButton;
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    public void loginOnClick()throws IOException
    {
        String finalURL = HttpUrl.parse("http://localhost:8080/web_Web_exploded/login")
                .newBuilder()
                .addQueryParameter("username", userNameTextField.getText())
                .build().toString();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(finalURL)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                System.out.println("Login failed");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                Platform.runLater(() ->{
                    System.out.println("Login successful");
                    FXMLLoader fxmlLoader = new FXMLLoader();
                    URL url = getClass().getResource("/mainSceneClient/mainSceneClient.fxml");
                    fxmlLoader.setLocation(url);
                    Parent root = null;
                    try {
                        root = fxmlLoader.load();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    Scene scene = new Scene(root);
                    stage.setScene(scene);
                    // Optionally, you can resize your new window here
                    stage.setWidth(700);
                    stage.setHeight(600);
                    mainController mainController = fxmlLoader.getController();
                    stage.setOnCloseRequest(event -> {
                        mainController.close();
                    });
                        }
                );

            }
        });
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        URL url = getClass().getResource("/mainSceneClient/mainSceneClient.fxml");
//        fxmlLoader.setLocation(url);
//        Parent root = fxmlLoader.load();
//        Scene scene = new Scene(root);
//        stage.setScene(scene);
//        // Optionally, you can resize your new window here
//        stage.setWidth(700);
//        stage.setHeight(600);
//        mainController mainController = fxmlLoader.getController();
//        stage.setOnCloseRequest(event -> {
//            mainController.close();
//        });
    }
}
