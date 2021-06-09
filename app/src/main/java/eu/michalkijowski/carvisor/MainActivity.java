package eu.michalkijowski.carvisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;

public class MainActivity extends AppCompatActivity {
    public static String BaseURL = "https://michal.vps.kronmar.net";
    public static CookieManager cookieManager;
    public static OkHttpClient defaultHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        defaultHttpClient = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager)).build();
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        requestPermissions(new String[] { Manifest.permission.WRITE_SETTINGS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET }, 1);
    }

    public void login(View view) {
        try {
            EditText login = (EditText) findViewById(R.id.editTextTextPersonName);
            EditText password = (EditText) findViewById(R.id.editTextTextPersonName2);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("login", login.getText().toString());
            jsonObject.put("password", password.getText().toString());
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder().url(MainActivity.BaseURL+"/API/authorization/authorize").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            if (response.code()==200)
            {
                Intent intent = new Intent(getApplicationContext(), ConnectWithDeviceToAdd.class);
                startActivity(intent);
            }
            else throw new SecurityException();
        } catch (IOException | SecurityException | JSONException e) {
            e.printStackTrace();
        }
    }
}