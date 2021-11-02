package eu.michalkijowski.carvisor.fragments.myFleet.add;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.list.MyFleetListFragment;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class MyFleetAddFragment extends Fragment {
    ImageView imageView;
    String image;

    public void selectImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        photoPickerIntent.setType("image/*");
        photoPickerIntent.putExtra("return-data", true);
        photoPickerIntent.putExtra("outputFormat", Bitmap.CompressFormat.PNG.toString());
        startActivityForResult(photoPickerIntent, 1003);
    }

    public boolean register(View view) {
        try {
            String name = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString().split(" ")[0];
            String surname = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString().split(" ")[1];
            String phone = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();
            String nick = ((EditText)getView().findViewById(R.id.editTextTextPersonName11)).getText().toString();
            String password1 = ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).getText().toString();
            String password2 = ((EditText)getView().findViewById(R.id.editTextTextPersonName13)).getText().toString();

            System.out.println(name);
            System.out.println(surname);
            System.out.println(phone);
            System.out.println(nick);
            System.out.println(password1);
            System.out.println(password2);
            System.out.println(image);

            if (!password1.equals(password2)) return false;
            if (name.equals("")) return false;
            if (surname.equals("")) return false;
            if (phone.equals("")) return false;
            if (nick.equals("")) return false;
            if (password1.equals("")) return false;

            if (image==null) return false;
            if (image.equals("")) return false;

            UserAddDTO userAddDTO = new UserAddDTO();
            userAddDTO.setName(name);
            userAddDTO.setSurname(surname);
            userAddDTO.setNick(nick);
            userAddDTO.setPhoneNumber(Integer.valueOf(phone));
            userAddDTO.setPassword(password1);
            userAddDTO.setImage(image);

            UsersService.addUser(userAddDTO);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
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
        View root = inflater.inflate(R.layout.fragment_my_fleet_add, container, false);
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
                if (register(view)) {
                    Toast.makeText(getContext(), "Poprawnie dodano użytkownika.", Toast.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    NavHostFragment.findNavController(MyFleetAddFragment.this)
                            .navigate(R.id.action_nav_my_fleet_add_to_nav_my_fleet,bundle);
                }
                else {
                    Toast.makeText(getContext(), "Nie wszystkie pola wypełniono poprawnie.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView.setImageResource(R.drawable.photo_add);
    }
}