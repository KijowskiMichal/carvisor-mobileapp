package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.ErrorsDTO;
import eu.michalkijowski.carvisor.data_models.ReverseGeocodingDTO;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class ReverseGeocodingService {
    public static ReverseGeocodingDTO getReverseGeocoding(double longitude, double latitude) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/track/reverseGeocoding/"+longitude+"/"+latitude+"/")
                .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            ReverseGeocodingDTO reverseGeocodingDTO = new Gson().fromJson(response.body().string(), ReverseGeocodingDTO.class);
            return  reverseGeocodingDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }
}