package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.EventDTO;
import eu.michalkijowski.carvisor.data_models.SafetiesDTO;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarService {
    public static EventDTO[] getEvents(int month, int year) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/calendar/get/"+month+"/"+year+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            EventDTO[] eventDTOS = new Gson().fromJson(response.body().string(), EventDTO[].class);
            return  eventDTOS;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void deleteEvent(Integer id) {
        try {
            Request request = new Request.Builder().delete()
                    .url(MainActivity.BaseURL + "/API/calendar/remove/"+id+"/")
                    .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            call.execute();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }
}