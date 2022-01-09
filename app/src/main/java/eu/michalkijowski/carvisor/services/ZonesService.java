package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.DevicesDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import eu.michalkijowski.carvisor.data_models.ZonesAddDTO;
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

    public static boolean addZone(ZonesAddDTO zonesAddDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(zonesAddDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/zones/add").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.code());
            System.out.println(response.body().string());
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