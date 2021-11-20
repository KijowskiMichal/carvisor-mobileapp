package eu.michalkijowski.carvisor.fragments.devices.edit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.UUID;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.DeviceAddDTO;
import eu.michalkijowski.carvisor.data_models.DeviceDataDTO;
import eu.michalkijowski.carvisor.data_models.DeviceEditDTO;
import eu.michalkijowski.carvisor.data_models.ImageDTO;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.services.DevicesService;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class DevicesEditFragment extends Fragment {
    ImageView imageView;
    String image;
    int id;
    private ProgressDialog mProgressDialog;

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

    public DeviceEditDTO edit(View view) {
        try {
            String licensePlate = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString();
            String brand = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();
            String model = ((EditText)getView().findViewById(R.id.editTextTextPersonName11)).getText().toString();
            String engine = ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).getText().toString();
            String year = ((EditText)getView().findViewById(R.id.editTextNumberDecimal2)).getText().toString();
            String norm = ((EditText)getView().findViewById(R.id.editTextNumberSigned)).getText().toString();
            String fuel = ((EditText)getView().findViewById(R.id.editTextTextPersonName10)).getText().toString();
            String tank = ((EditText)getView().findViewById(R.id.editTextNumberDecimal3)).getText().toString();
            String timeFrom = ((EditText)getView().findViewById(R.id.editTextTime)).getText().toString();
            String timeTo = ((EditText)getView().findViewById(R.id.editTextTime2)).getText().toString();

            if (licensePlate.equals("")) return null;
            if (brand.equals("")) return null;
            if (model.equals("")) return null;
            if (engine.equals("")) return null;
            if (year.equals("")) return null;
            if (norm.equals("")) return null;
            if (fuel.equals("")) return null;
            if (tank.equals("")) return null;
            if (timeFrom.equals("")) return null;
            if (timeTo.equals("")) return null;

            DeviceEditDTO deviceEditDTO = new DeviceEditDTO();
            deviceEditDTO.setLicensePlate(licensePlate);
            deviceEditDTO.setBrand(brand);
            deviceEditDTO.setModel(model);
            deviceEditDTO.setEngine(engine);
            deviceEditDTO.setYearOfProduction(Integer.valueOf(year));
            deviceEditDTO.setNorm(norm);
            deviceEditDTO.setFuel(fuel);
            deviceEditDTO.setTank(tank);
            deviceEditDTO.setTimeFrom(timeFrom);
            deviceEditDTO.setTimeTo(timeTo);

            return deviceEditDTO;
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
            new EditImage().execute(new ImageDTO(image));
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        this.id = bundle.getInt("id");
        new DownloadData().execute(bundle.getInt("id"));
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_my_devices_edit, container, false);
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
                DeviceEditDTO deviceEditDTO = edit(view);
                if (deviceEditDTO!=null) {
                    new EditDevice().execute(deviceEditDTO);
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
                NavHostFragment.findNavController(DevicesEditFragment.this)
                        .navigate(R.id.action_nav_devices_edit_to_nav_devices,bundle);
            }
        });
        return root;
    }

        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView.setImageResource(R.drawable.photo_add);
    }

    private class EditDevice extends AsyncTask<DeviceEditDTO, Void, Void> {
        @Override
        protected Void doInBackground(DeviceEditDTO... deviceEditDTO) {
            DevicesService.editDevice(deviceEditDTO[0], id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            mProgressDialog.dismiss();
            Toast.makeText(getContext(), "Poprawnie zedytowano pojazd.", Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            NavHostFragment.findNavController(DevicesEditFragment.this)
                    .navigate(R.id.action_nav_devices_edit_to_nav_devices,bundle);
        }
    }

    private class EditImage extends AsyncTask<ImageDTO, Void, Void> {
        @Override
        protected Void doInBackground(ImageDTO... imageDTO) {
            DevicesService.editDeviceImage(imageDTO[0], id);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.saving_image), true, false);
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            mProgressDialog.dismiss();
            Toast.makeText(getContext(), "Poprawnie zapisano zdjęcie.", Toast.LENGTH_LONG).show();
        }
    }

    private class DownloadData extends AsyncTask<Integer, Void, DeviceDataDTO> {
        @Override
        protected DeviceDataDTO doInBackground(Integer... integers) {
            return DevicesService.getDeviceData(integers[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(DeviceDataDTO deviceDataDTO) {
            super.onPostExecute(deviceDataDTO);

            if (deviceDataDTO==null) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "Nie masz uprawnień do edycji tego pojazdu.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(DevicesEditFragment.this)
                        .navigate(R.id.action_nav_my_fleet_edit_to_nav_my_fleet,bundle);
                mProgressDialog.dismiss();
                return;
            }

            ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).setText(deviceDataDTO.getLicensePlate());
            ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).setText(deviceDataDTO.getBrand());
            ((EditText)getView().findViewById(R.id.editTextTextPersonName11)).setText(deviceDataDTO.getModel());
            ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).setText(deviceDataDTO.getEngine());
            ((EditText)getView().findViewById(R.id.editTextNumberDecimal2)).setText(String.valueOf(deviceDataDTO.getYearOfProduction()));
            ((EditText)getView().findViewById(R.id.editTextNumberSigned)).setText(deviceDataDTO.getNorm());
            ((EditText)getView().findViewById(R.id.editTextTextPersonName10)).setText(deviceDataDTO.getFuel());
            ((EditText)getView().findViewById(R.id.editTextNumberDecimal3)).setText(deviceDataDTO.getTank());


            ((EditText)getView().findViewById(R.id.editTextTime)).setText(deviceDataDTO.getTimeFrom());
            ((EditText)getView().findViewById(R.id.editTextTime2)).setText(deviceDataDTO.getTimeTo());

            byte[] bytes = Base64.decode(deviceDataDTO.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
            Bitmap bitmap = ImageService.getCircleImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            image = "data:image/png;base64,"+ImageService.getBase64FromBitmap(bitmap);
            imageView.setImageBitmap(bitmap);

            mProgressDialog.dismiss();
        }
    }
}