package eu.michalkijowski.carvisor.services;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.ErrorsDTO;
import eu.michalkijowski.carvisor.data_models.NotificationsDTO;
import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationService {
    public static NotificationsDTO getNotifications(long timestampFrom, long timestampTo) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/notification/getNotification/"+timestampFrom+"/"+timestampTo+"/1/10000/")
                .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            NotificationsDTO notificationsDTO = new Gson().fromJson(response.body().string(), NotificationsDTO.class);
            return  notificationsDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String generateDescriptionOfNotification(Context context, String type, int value) {
        switch (type) {
            case "LEAVING_THE_ZONE":
                return context.getResources().getString(R.string.LEAVING_THE_ZONE)+" "+value+" m";
            case "SPEEDING":
                return context.getResources().getString(R.string.SPEEDING)+" "+value+" km/h";
        }
        return "";
    }
}