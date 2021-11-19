package eu.michalkijowski.carvisor;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import eu.michalkijowski.carvisor.activities.MainActivity;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NewDevice extends AppCompatActivity {

    public BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "CommsActivity";
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Dodawanie nowego urządzenia");
        setContentView(R.layout.activity_new_device);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final Intent intent = getIntent();
        final String address = intent.getStringExtra(ConnectWithDeviceToAdd.EXTRA_ADDRESS);

        final BluetoothDevice device = BTAdapter.getRemoteDevice(address);
        try {
            new ConnectThread(device).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // API 5+ solution
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addNewDevice(View view)
    {
        try {
            EditText licensePlate = (EditText) findViewById(R.id.editTextTextPersonName7);
            EditText brand = (EditText) findViewById(R.id.editTextTextPersonName8);
            EditText model = (EditText) findViewById(R.id.editTextTextPersonName9);
            EditText engine = (EditText) findViewById(R.id.editTextTextPersonName4);
            EditText fuelType = (EditText) findViewById(R.id.editTextTextPersonName3);
            EditText tank = (EditText) findViewById(R.id.editTextNumber);
            EditText norm = (EditText) findViewById(R.id.editTextNumberDecimal);
            String password = RandomStringUtils.random(10, true, true);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("licensePlate", licensePlate.getText().toString());
            jsonObject.put("brand", brand.getText().toString());
            jsonObject.put("model", model.getText().toString());
            jsonObject.put("engine", engine.getText().toString());
            jsonObject.put("fuel", fuelType.getText().toString());
            jsonObject.put("tank", tank.getText().toString());
            jsonObject.put("norm", norm.getText().toString());
            jsonObject.put("password", password);
            RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());
            Request request = new Request.Builder().url(MainActivity.BaseURL + "/API/devices/addDevice").post(body).build();
            Call call = MainActivity.defaultHttpClient.newCall(request);
            Response response = call.execute();
            JSONObject jsonToCar = new JSONObject();
            jsonToCar.put("address", MainActivity.BaseURL);
            jsonToCar.put("licensePlate", licensePlate.getText().toString());
            jsonToCar.put("password", password);
            if (response.code() == 201) {
                Toast.makeText(getApplicationContext(), "Dodano", Toast.LENGTH_LONG).show();
                OutputStream mmOutputStream = mmSocket.getOutputStream();
                mmOutputStream.write(jsonToCar.toString().getBytes());

                /*InputStream mmInputStream = mmSocket.getInputStream();
                byte[] buffer = new byte[10];
                int bytes;

                try {
                    bytes = mmInputStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    Log.v("Status", readMessage);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/

                mmOutputStream.close();
            } else
            {
                Toast.makeText(getApplicationContext(), "Taki pojazd już istnieje", Toast.LENGTH_LONG).show();
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public class ConnectThread extends Thread {
        @RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
        public ConnectThread(BluetoothDevice device) throws IOException {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                UUID uuid = UUID.fromString("94f39d29-7d6d-437d-973b-fba39e49d4ee");
                tmp = mmDevice.createRfcommSocketToServiceRecord(uuid);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
            BTAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.v(TAG, "Connection exception!");
                try {
                    mmSocket.close();
                } catch (IOException closeException) {

                }
            }
        }
    }
}