package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.AuthorizationDTO;
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
}