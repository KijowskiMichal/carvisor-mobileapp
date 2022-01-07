package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Date;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.DevicesDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import eu.michalkijowski.carvisor.data_models.SummaryDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SummaryService {
    public static SummaryDTO getSummaryData() {
        try {
            Date date = new Date();
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/ranking/getUserSummary/"+(date.getTime()/1000-(3600*24*14))+"/"+date.getTime()/1000+"/1/10000/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            SummaryDTO summaryDTO = new Gson().fromJson(response.body().string(), SummaryDTO.class);
            return  summaryDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}