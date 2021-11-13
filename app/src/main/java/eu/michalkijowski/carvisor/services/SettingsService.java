package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.AuthorizationDTO;
import eu.michalkijowski.carvisor.data_models.GlobalSettingsDTO;
import eu.michalkijowski.carvisor.data_models.SetGlobalSettingsDTO;
import eu.michalkijowski.carvisor.data_models.UsersDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SettingsService {
    public static GlobalSettingsDTO getGlobalConfiguration() {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/carConfiguration/getGlobalConfiguration/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            GlobalSettingsDTO globalSettingsDTO = new Gson().fromJson(response.body().string(), GlobalSettingsDTO.class);
            return  globalSettingsDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static boolean setGlobalConfiguration(SetGlobalSettingsDTO setGlobalSettingsDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(setGlobalSettingsDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/carConfiguration/setGlobalConfiguration").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            if (response.code()==200)
            {
                return true;
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }
}