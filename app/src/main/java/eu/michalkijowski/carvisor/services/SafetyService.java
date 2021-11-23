package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.EcopointsDTO;
import eu.michalkijowski.carvisor.data_models.SafetiesDTO;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class SafetyService {
    public static SafetiesDTO getSafetyList(String regex) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/safetyPoints/list/1/10000/"+(regex.equals("") ? "$" : regex)+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            SafetiesDTO safetiesDTO = new Gson().fromJson(response.body().string(), SafetiesDTO.class);
            return  safetiesDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}