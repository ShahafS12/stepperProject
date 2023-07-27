package topClientScene;


import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import users.UserImpl;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class isManagerRefresher extends TimerTask
{
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<Boolean> isManagerConsumer;
    private final Consumer<List<String>> rolesConsumer;
    private int requestNumber;
    private String userName;
    public isManagerRefresher(String userName, Consumer<String> httpRequestLoggerConsumer, Consumer<Boolean> isManagerConsumer, Consumer<List<String>> rolesConsumer) {
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.isManagerConsumer = isManagerConsumer;
        this.userName = userName;
        this.rolesConsumer = rolesConsumer;
        requestNumber = 0;
    }
    @Override
    public void run(){
        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + "IS_MANAGER" + " | Top Request # " + finalRequestNumber);
        String finalUrl = HttpUrl.parse(Constants.GET_USER).newBuilder()
                .addQueryParameter("userName", userName)
                .build().toString();
        HttpClientUtil.runAsync(finalUrl, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                httpRequestLoggerConsumer.accept("Top Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String jsonArray = response.body().string();
                httpRequestLoggerConsumer.accept("Top Request # " + finalRequestNumber + " | Response: " + jsonArray);
                UserImpl user = new Gson().fromJson(jsonArray, UserImpl.class);
                List<String> rolesNames = user.getRolesNames();
                Platform.runLater(() -> {
                    rolesConsumer.accept(rolesNames);
                });
                Boolean isManager = user.isManager();
                if(isManager!=null){
                    Platform.runLater(() -> {
                        isManagerConsumer.accept(isManager);
                    });
                }
            }
        });
    }
}
