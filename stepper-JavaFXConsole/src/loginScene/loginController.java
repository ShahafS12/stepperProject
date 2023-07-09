package loginScene;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import mainSceneClient.mainClientController;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static util.Constants.*;

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
    private final StringProperty errorMessageProperty = new SimpleStringProperty();
    public void setStage(Stage stage)
    {
        this.stage = stage;
    }
    public void loginOnClick()throws IOException
    {
        String finalURL = HttpUrl.parse(LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userNameTextField.getText())
                .build().toString();
        HttpClientUtil.runAsync(finalURL, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                System.out.println("Login failed");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)//todo check how to print the error message to screen
                    );
                } else {
                    Platform.runLater(() -> {
                                System.out.println("Login successful");
                                FXMLLoader fxmlLoader = new FXMLLoader();
                                URL url = getClass().getResource(MAIN_PAGE_CLIENT_FXML_RESOURCE_LOCATION);
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
                                mainClientController mainClientController = fxmlLoader.getController();
                                mainClientController.updateUserName(userNameTextField.getText());
                                stage.setOnCloseRequest(event -> {
                                    mainClientController.close();
                                });
                            }
                    );

                }
            }
        });
    }
}
