package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.DevicesDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import eu.michalkijowski.carvisor.data_models.ZonesDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ZonesService {
    public static ZonesDTO[] getZones(String regex) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/zones/list/"+(regex.equals("") ? "$" : regex)+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            ZonesDTO[] zonesDTOS = new Gson().fromJson(response.body().string(), ZonesDTO[].class);
            return  zonesDTOS;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}