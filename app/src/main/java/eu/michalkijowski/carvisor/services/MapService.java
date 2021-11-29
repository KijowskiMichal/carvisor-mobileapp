package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.DeviceNamesDTO;
import eu.michalkijowski.carvisor.data_models.DevicesDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import eu.michalkijowski.carvisor.data_models.MapWrapperDTO;
import eu.michalkijowski.carvisor.data_models.UserNamesDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MapService {
    public static UserNamesDTO[] getUserList(String regex) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/users/listUserNames/"+(regex.equals("") ? "$" : regex)+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            UserNamesDTO[] userNamesDTOS = new Gson().fromJson(response.body().string(), UserNamesDTO[].class);
            return  userNamesDTOS;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DeviceNamesDTO[] getDeviceList(String regex) {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/devices/listDevicesNames/"+(regex.equals("") ? "$" : regex)+"/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            DeviceNamesDTO[] deviceNamesDTOS = new Gson().fromJson(response.body().string(), DeviceNamesDTO[].class);
            return  deviceNamesDTOS;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static MapWrapperDTO getMapFromUser(int id, long timestamp) {
        try {
            System.out.println(MainActivity.BaseURL + "/API/track/getTrackData/"+id+"/"+timestamp+"/");
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/track/getTrackData/"+id+"/"+timestamp+"/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            MapWrapperDTO mapWrapperDTO = new Gson().fromJson(response.body().string(), MapWrapperDTO.class);
            return  mapWrapperDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}