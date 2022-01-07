package eu.michalkijowski.carvisor.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import java.net.CookieManager;
import java.net.CookiePolicy;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.AuthorizationDTO;
import eu.michalkijowski.carvisor.data_models.EventEditDTO;
import eu.michalkijowski.carvisor.fragments.calendar.edit.CalendarEditFragment;
import eu.michalkijowski.carvisor.services.AuthorizationService;
import eu.michalkijowski.carvisor.services.CalendarService;
import eu.michalkijowski.carvisor.services.utils.ResponseInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.JavaNetCookieJar;

public class ServerChooseActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_choose);
    }

    public void login(View view) {
        EditText server = (EditText) findViewById(R.id.editTextTextPersonName);
        new CheckConnection().execute(prepareLink(server.getText().toString()));
    }

    private class CheckConnection extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                return AuthorizationService.connectionTest(strings[0]);
            }
            catch (IllegalArgumentException e) {
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(ServerChooseActivity.this, "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            mProgressDialog.dismiss();
            if (bool) {
                Toast.makeText(getApplicationContext(), "Połączono.", Toast.LENGTH_LONG).show();
                MainActivity.BaseURL = prepareLink(((EditText) findViewById(R.id.editTextTextPersonName)).getText().toString());
                MainActivity.sharedPref.edit().putString("BASEURL", MainActivity.BaseURL).commit();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
            else {
                Toast.makeText(getApplicationContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String prepareLink(String link) {
        link = link.replace("https://", "");
        link = link.replace("http://", "");
        return "https://"+link;
    }
}