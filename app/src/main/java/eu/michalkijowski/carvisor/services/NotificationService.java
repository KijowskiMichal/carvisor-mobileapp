package eu.michalkijowski.carvisor.services;

import android.content.Context;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.ErrorsDTO;
import eu.michalkijowski.carvisor.data_models.NewNotificationDTO;
import eu.michalkijowski.carvisor.data_models.NotificationMessageDTO;
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
            case "OVER_HOURS":
                return context.getResources().getString(R.string.OVER_HOURS);
        }
        return "";
    }

    public static String generateWatchwordOfNotification(Context context, String type) {
        switch (type) {
            case "LEAVING_THE_ZONE":
                return context.getResources().getString(R.string.LEAVING_THE_ZONE_WATCHWORD);
            case "SPEEDING":
                return context.getResources().getString(R.string.SPEEDING_WATCHWORD);
            case "OVER_HOURS":
                return context.getResources().getString(R.string.OVER_HOURS_WATCHWORD);
        }
        return "";
    }


    public static NewNotificationDTO[] getNewNotifications() {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/notification/newNotifications")
                    .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            NewNotificationDTO[] newNotificationDTOS = new Gson().fromJson(response.body().string(), NewNotificationDTO[].class);
            return  newNotificationDTOS;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<NotificationMessageDTO> getNotificationMessageList(Context context) {
        NewNotificationDTO[] newNotificationDTOList = getNewNotifications();
        ArrayList<NotificationMessageDTO> notificationMessageDTOArrayList = new ArrayList<>();
        for (NewNotificationDTO newNotificationDTO : newNotificationDTOList) {
            NotificationMessageDTO notificationMessageDTO = new NotificationMessageDTO();
            notificationMessageDTO.setTitle(generateWatchwordOfNotification(context, newNotificationDTO.getType()));
            notificationMessageDTO.setContent(generateDescriptionOfNotification(context, newNotificationDTO.getType(), newNotificationDTO.getValue()));
            notificationMessageDTOArrayList.add(notificationMessageDTO);
        }
        return notificationMessageDTOArrayList;
    }
}