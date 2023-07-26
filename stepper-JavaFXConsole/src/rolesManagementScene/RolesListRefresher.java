package rolesManagementScene;

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

public class RolesListRefresher  extends TimerTask
{
    private final Consumer<String> httpRequestLoggerConsumer;
    private final Consumer<List<String>> usersListConsumer;
    private int requestNumber;
    private final BooleanProperty shouldUpdate;

    public RolesListRefresher(BooleanProperty shouldUpdate, Consumer<String> httpRequestLoggerConsumer, Consumer<List<String>> usersListConsumer) {
        this.shouldUpdate = shouldUpdate;
        this.httpRequestLoggerConsumer = httpRequestLoggerConsumer;
        this.usersListConsumer = usersListConsumer;
        requestNumber = 0;
    }
    @Override
    public void run(){
        final int finalRequestNumber = ++requestNumber;
        httpRequestLoggerConsumer.accept("About to invoke: " + Constants.ROLES_LIST + " | Users Request # " + finalRequestNumber);
        HttpClientUtil.runAsync(Constants.ROLES_LIST, new Callback()
        {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e)
            {
                httpRequestLoggerConsumer.accept("Users Request # " + finalRequestNumber + " | Ended with failure...");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException
            {
                String jsonArrayOfRolesNames = response.body().string();
                httpRequestLoggerConsumer.accept("Roles Request # " + finalRequestNumber + " | Response: " + jsonArrayOfRolesNames);
                String[] rolesNames = new Gson().fromJson(jsonArrayOfRolesNames, String[].class);
                if(rolesNames!=null){
                    Platform.runLater(() -> {
                        usersListConsumer.accept(Arrays.asList(rolesNames));
                    });
                }
            }
        });
    }
}
