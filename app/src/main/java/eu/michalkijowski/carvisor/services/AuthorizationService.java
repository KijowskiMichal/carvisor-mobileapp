package eu.michalkijowski.carvisor.services;

import android.content.Intent;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import eu.michalkijowski.carvisor.ConnectWithDeviceToAdd;
import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.AuthorizationDTO;
import eu.michalkijowski.carvisor.data_models.AuthorizationStatusDTO;
import eu.michalkijowski.carvisor.data_models.EventDTO;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AuthorizationService {
    public static boolean authorizeUser(AuthorizationDTO authorizationDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(authorizationDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/authorization/authorize").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            if (response.code()==200)
            {
                return true;
            }
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void logout() {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/authorization/logout/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            call.execute();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static AuthorizationStatusDTO getAuthorizationStatus() {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/authorization/status/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            AuthorizationStatusDTO authorizationStatusDTO = new Gson().fromJson(response.body().string(), AuthorizationStatusDTO.class);
            return  authorizationStatusDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean connectionTest(String link) {
        try {
            Request request = new Request.Builder()
                    .url(link + "/API/authorization/status/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            return  response.code()==200;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return false;
    }
}