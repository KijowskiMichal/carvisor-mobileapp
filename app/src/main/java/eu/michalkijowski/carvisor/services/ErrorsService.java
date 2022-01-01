package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.EcopointsDTO;
import eu.michalkijowski.carvisor.data_models.ErrorsDTO;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class ErrorsService {
    public static ErrorsDTO getErrorsList(long timestampFrom, long timestampTo) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/errors/getErrors/"+timestampFrom+"/"+timestampTo+"/1/10000/")
                .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            ErrorsDTO errorsDTO = new Gson().fromJson(response.body().string(), ErrorsDTO.class);
            return  errorsDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}