package topClientScene;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
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

public class topClientRefresher extends TimerTask
{
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<String>> rolesConsumer;
    private int requestNumber;
    private UserImpl user;
    public topClientRefresher(Consumer<String> httpRequestLoggerConsumer, Consumer<List<String>> rolesConsumer, UserImpl user) {
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.rolesConsumer = rolesConsumer;
        requestNumber = 0;
    }
    @Override
    public void run(){
        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + Constants.FLOW_LIST + " | Top Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(Constants.ROLES_LIST, new Callback()
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
                String[] rolesNames = new Gson().fromJson(jsonArray, String[].class);
                if(rolesNames!=null){
                    Platform.runLater(() -> {
                        rolesConsumer.accept(Arrays.asList(rolesNames));
                    });
                }
            }
        });
    }
}
