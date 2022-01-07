package eu.michalkijowski.carvisor.fragments.devices.add;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import eu.michalkijowski.carvisor.ConnectWithDeviceToAdd;
import eu.michalkijowski.carvisor.NewDevice;
import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.MainActivity;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.RegisterDTO;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.services.DevicesService;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class DevicesAddFragment extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;
    public BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
    private static final String TAG = "CommsActivity";
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    String licensePlate;
    String password;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("return-data", true);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(photoPickerIntent, 1003);
    }

    public DeviceAddDTO addDevice(View view) {
        try {
            licensePlate = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString();
            String brand = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();
            String model = ((EditText)getView().findViewById(R.id.editTextTextPersonName11)).getText().toString();
            String engine = ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).getText().toString();
            String year = ((EditText)getView().findViewById(R.id.editTextNumberDecimal2)).getText().toString();
            String norm = ((EditText)getView().findViewById(R.id.editTextNumberSigned)).getText().toString();
            String fuel = ((EditText)getView().findViewById(R.id.editTextTextPersonName10)).getText().toString();
            String tank = ((EditText)getView().findViewById(R.id.editTextNumberDecimal3)).getText().toString();

            if (licensePlate.equals("")) return null;
            if (brand.equals("")) return null;
            if (model.equals("")) return null;
            if (engine.equals("")) return null;
            if (year.equals("")) return null;
            if (norm.equals("")) return null;
            if (fuel.equals("")) return null;
            if (tank.equals("")) return null;

            if (image==null) return null;
            if (image.equals("")) return null;

            password = UUID.randomUUID().toString();

            DeviceAddDTO deviceAddDTO = new DeviceAddDTO();
            deviceAddDTO.setLicensePlate(licensePlate);
            deviceAddDTO.setBrand(brand);
            deviceAddDTO.setModel(model);
            deviceAddDTO.setEngine(engine);
            deviceAddDTO.setYearOfProduction(Integer.valueOf(year));
            deviceAddDTO.setNorm(norm);
            deviceAddDTO.setFuel(fuel);
            deviceAddDTO.setTank(tank);
            deviceAddDTO.setImage(image);
            deviceAddDTO.setPassword(password);

            return deviceAddDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == 1003) {
            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap.getWidth()>bitmap.getHeight()) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int)Math.floor((400.0/bitmap.getHeight())*bitmap.getWidth()), 400, true);
                }
                else {
                    bitmap = Bitmap.createScaledBitmap(bitmap, 400, (int)Math.floor((400.0/bitmap.getWidth())*bitmap.getHeight()), true);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, 400, 400);
                image = "data:image/png;base64,"+ImageService.getBase64FromBitmap(bitmap);
                bitmap = ImageService.getCircleImage(bitmap);
                imageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_my_devices_add, container, false);
        /********************************
         * Image selector
         *******************************/
        imageView = root.findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage(view);
            }
        });
        /********************************
         * Image selector
         *******************************/
        Button registerButton = root.findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeviceAddDTO deviceAddDTO = addDevice(view);
                if (deviceAddDTO!=null) {
                    new PostNewDevice().execute(deviceAddDTO);
                }
                else {
                    Toast.makeText(getContext(), "Nie wszystkie pola wypełniono poprawnie.", Toast.LENGTH_LONG).show();
                }
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(DevicesAddFragment.this)
                        .navigate(R.id.action_nav_devices_add_form_to_nav_devices_add,bundle);
            }
        });
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final String address = this.getArguments().getString("address");
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
        return root;
    }

        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView.setImageResource(R.drawable.photo_add);
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

    private class PostNewDevice extends AsyncTask<DeviceAddDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(DeviceAddDTO... deviceAddDTOS) {
            return DevicesService.addDevice(deviceAddDTOS[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            if (bool) {
                try {
                    RegisterDTO registerDTO = new RegisterDTO();
                    registerDTO.setAddress(MainActivity.BaseURL);
                    registerDTO.setLicensePlate(licensePlate);
                    registerDTO.setPassword(password);
                    OutputStream mmOutputStream = mmSocket.getOutputStream();
                    Gson gson = new Gson();
                    mmOutputStream.write(gson.toJson(registerDTO).toString().getBytes());

                    InputStream mmInputStream = mmSocket.getInputStream();
                    byte[] buffer = new byte[10];
                    int bytes;

                    try {
                        bytes = mmInputStream.read(buffer);
                        String readMessage = new String(buffer, 0, bytes);
                        Log.v("Status", readMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    mmOutputStream.close();
                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), "Poprawnie dodano pojazd.", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    NavHostFragment.findNavController(DevicesAddFragment.this)
                            .navigate(R.id.action_nav_my_fleet_add_to_nav_my_fleet,bundle);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "Taki pojazd już istnieje.", Toast.LENGTH_LONG).show();
            }
        }
    }
}