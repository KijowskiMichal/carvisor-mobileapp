package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.DevicesDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.data_models.UsersDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DevicesService {
    public static DevicesDTO getDevicesList(String regex) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/devices/list/1/10000/"+(regex.equals("") ? "$" : regex)+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            DevicesDTO devicesDTO = new Gson().fromJson(response.body().string(), DevicesDTO.class);
            return  devicesDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addDevice(DeviceAddDTO deviceAddDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(deviceAddDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/devices/addDevice").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.code());
            System.out.println(response.body().string());
            if (response.code()==201)
            {
                return true;
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static DeviceDataDTO getDeviceData(int id) {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/devices/getDeviceData/"+id+"/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            if (response.code()!=200)
            {
                return null;
            }
            DeviceDataDTO deviceDataDTO = new Gson().fromJson(response.body().string(), DeviceDataDTO.class);
            return deviceDataDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void editDevice(DeviceEditDTO deviceEditDTO, int id) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(deviceEditDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/devices/changeDeviceData/"+id+"/").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            call.execute();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void editDeviceImage(ImageDTO imageDTO, int id) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(imageDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/devices/changeDeviceImage/"+id+"/").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            call.execute();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void deleteDevice(Integer id) {
        try {
            Request request = new Request.Builder().delete()
                    .url(MainActivity.BaseURL + "/API/devices/removeDevice/"+id+"/")
                    .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.code());
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }
}