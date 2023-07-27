package userManagementScene;

import java.io.IOException;
import java.util.TimerTask;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.google.gson.Gson;
import okhttp3.Response;
import util.Constants;
import util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import org.jetbrains.annotations.NotNull;

public class UserListRefresher  extends TimerTask
{
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<String>> usersListConsumer;
    private int requestNumber;
    public UserListRefresher(Consumer<String> httpRequestLoggerConsumer, Consumer<List<String>> usersListConsumer) {
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.usersListConsumer = usersListConsumer;
        requestNumber = 0;
    }
    @Override
    public void run(){
        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + Constants.USER_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(Constants.USER_LIST, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String jsonArrayOfUsersNames = response.body().string();
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);
                String[] usersNames = new Gson().fromJson(jsonArrayOfUsersNames, String[].class);
                if(usersNames!=null){
                    Platform.runLater(() -> {
                        usersListConsumer.accept(Arrays.asList(usersNames));
                    });
                }
            }
        });
    }
}
