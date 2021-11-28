package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.AuthorizationDTO;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.ReportAddDTO;
import eu.michalkijowski.carvisor.data_models.ReportDTO;
import eu.michalkijowski.carvisor.data_models.UserNamesDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReportsService {
    public static ReportDTO[] getReportsList(String regex) {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/raports/list/1/10000/"+(regex.equals("") ? "$" : regex)+"/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            ReportDTO[] reportDTOS = new Gson().fromJson(response.body().string(), ReportDTO[].class);
            return  reportDTOS;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public static boolean addReport(ReportAddDTO reportAddDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(reportAddDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/raports/add").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            if (response.code()==201)
            {
                return true;
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }
}