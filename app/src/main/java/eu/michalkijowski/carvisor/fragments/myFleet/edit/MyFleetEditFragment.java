package eu.michalkijowski.carvisor.fragments.myFleet.edit;

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

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class MyFleetEditFragment extends Fragment {
    ImageView imageView;
    String image;
    String userPrivilages;
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

    public UserDataDTO edit(View view) {
        try {
            String name = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString();
            String phone = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();

            if (name.equals("")) return null;
            if (phone.equals("")) return null;

            if (image==null) return null;
            if (image.equals("")) return null;

            UserDataDTO userDataDTO = new UserDataDTO();
            userDataDTO.setName(name);
            userDataDTO.setPhoneNumber(phone);
            userDataDTO.setImage(image);
            userDataDTO.setUserPrivileges(userPrivilages);

            return userDataDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkIfUserWantToChangePassword(View view) {
        String password1 = ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).getText().toString();
        String password2 = ((EditText)getView().findViewById(R.id.editTextTextPersonName13)).getText().toString();

        if ((!password1.equals("")) || (!password2.equals(""))) return true;
        return false;
    }

    public UserPasswordDTO changePassword(View view) {
        try {
            String password1 = ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).getText().toString();
            String password2 = ((EditText)getView().findViewById(R.id.editTextTextPersonName13)).getText().toString();

            if (!password1.equals(password2)) return null;

            UserPasswordDTO userPasswordDTO = new UserPasswordDTO();
            userPasswordDTO.setFirstPassword(password1);
            userPasswordDTO.setSecondPassword(password2);

            return userPasswordDTO;
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
        Bundle bundle = this.getArguments();
        this.id = bundle.getInt("id");
        new DownloadData().execute(bundle.getInt("id"));
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_my_fleet_edit, container, false);
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
                UserDataDTO userDataDTO = edit(view);
                if (userDataDTO!=null) {
                    if (checkIfUserWantToChangePassword(view)) {
                        UserPasswordDTO userPasswordDTO = changePassword(view);
                        if (userPasswordDTO!=null) {
                            new EditUserPassword().execute(userPasswordDTO);
                            new EditUser().execute(userDataDTO);
                        }
                        else {
                            Toast.makeText(getContext(), "Hasła nie są identyczne.", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        new EditUser().execute(userDataDTO);
                    }
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
                NavHostFragment.findNavController(MyFleetEditFragment.this)
                        .navigate(R.id.action_nav_my_fleet_edit_to_nav_my_fleet,bundle);
            }
        });
        return root;
    }

        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView.setImageResource(R.drawable.photo_add);
    }

    private class EditUserPassword extends AsyncTask<UserPasswordDTO, Void, Void> {
        @Override
        protected Void doInBackground(UserPasswordDTO... userPasswordDTO) {
            UsersService.editUserPassword(userPasswordDTO[0], id);
            return null;
        }
    }

    private class EditUser extends AsyncTask<UserDataDTO, Void, Void> {
        @Override
        protected Void doInBackground(UserDataDTO... userDataDTO) {
            UsersService.editUser(userDataDTO[0], id);
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
            Toast.makeText(getContext(), "Poprawnie zedytowano użytkownika.", Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            NavHostFragment.findNavController(MyFleetEditFragment.this)
                    .navigate(R.id.action_nav_my_fleet_edit_to_nav_my_fleet,bundle);
        }
    }

    private class DownloadData extends AsyncTask<Integer, Void, UserDataDTO> {
        @Override
        protected UserDataDTO doInBackground(Integer... integers) {
            return UsersService.getUserData(integers[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(UserDataDTO userDataDTO) {
            super.onPostExecute(userDataDTO);

            if (userDataDTO==null) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "Nie masz uprawnień do edycji tego użytkownika.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(MyFleetEditFragment.this)
                        .navigate(R.id.action_nav_my_fleet_edit_to_nav_my_fleet,bundle);
                mProgressDialog.dismiss();
                return;
            }

            userPrivilages = userDataDTO.getUserPrivileges();

            ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).setText(userDataDTO.getName());
            ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).setText(userDataDTO.getPhoneNumber());

            byte[] bytes = Base64.decode(userDataDTO.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
            Bitmap bitmap = ImageService.getCircleImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
            image = "data:image/png;base64,"+ImageService.getBase64FromBitmap(bitmap);
            imageView.setImageBitmap(bitmap);

            mProgressDialog.dismiss();
        }
    }
}