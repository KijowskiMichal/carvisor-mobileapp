package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.EventAddDTO;
import eu.michalkijowski.carvisor.data_models.EventDTO;
import eu.michalkijowski.carvisor.data_models.EventEditDTO;
import eu.michalkijowski.carvisor.data_models.SafetiesDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
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

    public static boolean addEvent(EventAddDTO eventAddDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(eventAddDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/calendar/add").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.code());
            System.out.println(response.body().string());
            System.out.println(body.toString());
            if (response.code()==200)
            {
                return true;
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static EventDTO getEvent(int id) {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/calendar/getEvent/"+id+"/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            EventDTO eventDTO = new Gson().fromJson(response.body().string(), EventDTO.class);
            return  eventDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean editEvent(EventEditDTO eventEditDTO, int id) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(eventEditDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/calendar/updateEvent/"+eventEditDTO.getId()+"/").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(MainActivity.BaseURL+"/API/calendar/updateEvent/"+id+"/");
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