package eu.michalkijowski.carvisor.services;

import com.google.gson.Gson;

import java.io.IOException;

import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.data_models.UsersDTO;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

public class UsersService {
    public static UsersDTO getUsersList(String regex) {
        try {
            Request request = new Request.Builder()
                .url(MainActivity.BaseURL + "/API/users/list/1/10000/"+(regex.equals("") ? "$" : regex)+"/")
                .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            UsersDTO usersDTO = new Gson().fromJson(response.body().string(), UsersDTO.class);
            return  usersDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean addUser(UserAddDTO userAddDTO) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(userAddDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/users/addUser").post(body).build();
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

    public static void editUser(UserDataDTO userDataDTO, int id) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(userDataDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/users/changeUserData/"+id+"/").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            call.execute();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static UserDataDTO getUserData(int id) {
        try {
            Request request = new Request.Builder()
                    .url(MainActivity.BaseURL + "/API/users/getUserData/"+id+"/")
                    .build();

            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            if (response.code()!=200)
            {
                return null;
            }
            UserDataDTO userDataDTO = new Gson().fromJson(response.body().string(), UserDataDTO.class);
            return userDataDTO;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void editUserPassword(UserPasswordDTO userPasswordDTO, int id) {
        Gson gson = new Gson();
        try {
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(userPasswordDTO));
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/users/changePassword/"+id+"/").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            call.execute();
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUser(Integer id) {
        try {
            Request request = new Request.Builder().delete()
                    .url(MainActivity.BaseURL + "/API/users/removeUser/"+id+"/")
                    .build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            System.out.println(response.code());
        } catch (IOException | SecurityException e) {
            e.printStackTrace();
        }
    }
}