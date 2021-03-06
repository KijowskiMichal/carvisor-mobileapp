package eu.michalkijowski.carvisor.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.AuthorizationDTO;
import eu.michalkijowski.carvisor.services.AuthorizationService;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.JavaNetCookieJar;
import eu.michalkijowski.carvisor.services.utils.ResponseInterceptor;

public class MainActivity extends AppCompatActivity {
    public static String BaseURL;
    public static CookieManager cookieManager;
    public static OkHttpClient defaultHttpClient;
    public static boolean firstUse = true;
    public static SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        defaultHttpClient = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(cookieManager)).addInterceptor(new ResponseInterceptor()).build();
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (MainActivity.firstUse) {
            requestPermissions(new String[] { Manifest.permission.WRITE_SETTINGS, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.INTERNET }, 1);
            MainActivity.firstUse = false;
        }
        if (sharedPref==null) {
            sharedPref = getSharedPreferences("carvisor", MODE_PRIVATE);
            if (sharedPref.getString("BASEURL", "EMPTY").equals("EMPTY")) {
                Intent intent = new Intent(getApplicationContext(), ServerChooseActivity.class);
                startActivity(intent);
            }
            else {
                BaseURL = sharedPref.getString("BASEURL", null);
            }
        }
    }

    public void login(View view) {
        EditText login = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText password = (EditText) findViewById(R.id.editTextTextPersonName2);
        if (AuthorizationService.authorizeUser(new AuthorizationDTO(login.getText().toString(), password.getText().toString())))
        {
            AuthorizationService.authorizationStatus = AuthorizationService.getAuthorizationStatus();
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.wrong_password, Toast.LENGTH_LONG).show();
        }
    }
}