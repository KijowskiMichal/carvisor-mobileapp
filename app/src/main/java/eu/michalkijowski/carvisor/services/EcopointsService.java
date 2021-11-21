package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.DevicesDTO;
import eu.michalkijowski.carvisor.data_models.EcopointsDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EcopointsService {
    public static EcopointsDTO getEcopointsList(String regex) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/ecoPoints/list/1/10000/"+(regex.equals("") ? "$" : regex)+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            EcopointsDTO ecopointsDTO = new Gson().fromJson(response.body().string(), EcopointsDTO.class);
            return  ecopointsDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}