package showFlowScene;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import util.Constants;
import util.http.HttpClientUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.function.Consumer;

public class FlowListRefresher extends TimerTask
{
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<String>> usersListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;


    public FlowListRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<List<String>> usersListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.usersListConsumer = usersListConsumer;
        requestNumber = 0;
    }

    @Override
    public void run() {

        //if (!shouldUpdate.get()) {
        //    return;
        //}
        if(Constants.FLOW_LIST == null){
            return;
        }

        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + Constants.FLOW_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(Constants.FLOW_LIST, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");

            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.code() != 200){
                    httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
                    return;
                }
                String jsonArrayOfUsersNames = response.body().string();
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Response: " + jsonArrayOfUsersNames);
                if(!jsonArrayOfUsersNames.equals("")){
                    String[] usersNames = new Gson().fromJson(jsonArrayOfUsersNames, String[].class);
                    if(usersNames!=null){
                        Platform.runLater(() -> {
                            usersListConsumer.accept(Arrays.asList(usersNames));
                        });
                    }
                }
            }
        });
    }
}
