package eu.michalkijowski.carvisor.fragments.calendar.add;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class CalendarAddFragment extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public UserAddDTO register(View view) {
        try {
            String name = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString().split(" ")[0];
            String surname = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString().split(" ")[1];
            String phone = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();
            String nick = ((EditText)getView().findViewById(R.id.editTextTextPersonName11)).getText().toString();
            String password1 = ((EditText)getView().findViewById(R.id.editTextTextPersonName12)).getText().toString();
            String password2 = ((EditText)getView().findViewById(R.id.editTextTextPersonName13)).getText().toString();

            if (!password1.equals(password2)) return null;
            if (name.equals("")) return null;
            if (surname.equals("")) return null;
            if (phone.equals("")) return null;
            if (nick.equals("")) return null;
            if (password1.equals("")) return null;

            if (image==null) return null;
            if (image.equals("")) return null;

            UserAddDTO userAddDTO = new UserAddDTO();
            userAddDTO.setName(name);
            userAddDTO.setSurname(surname);
            userAddDTO.setNick(nick);
            userAddDTO.setPhoneNumber(Integer.valueOf(phone));
            userAddDTO.setPassword(password1);
            userAddDTO.setImage(image);

            return userAddDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_my_fleet_add, container, false);
        Button registerButton = root.findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserAddDTO userAddDTO = register(view);
                if (userAddDTO!=null) {
                    new PostNewUser().execute(userAddDTO);
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
                NavHostFragment.findNavController(CalendarAddFragment.this)
                        .navigate(R.id.action_nav_my_fleet_add_to_nav_my_fleet,bundle);
            }
        });
        return root;
    }

        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        imageView.setImageResource(R.drawable.photo_add);
    }

    private class PostNewUser extends AsyncTask<UserAddDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(UserAddDTO... userAddDTO) {
            return UsersService.addUser(userAddDTO[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            mProgressDialog.dismiss();
            if (bool) {
                Toast.makeText(getContext(), "Poprawnie dodano użytkownika.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(CalendarAddFragment.this)
                        .navigate(R.id.action_nav_my_fleet_add_to_nav_my_fleet,bundle);
            }
            else {
                Toast.makeText(getContext(), "Taki użytkownik już istnieje.", Toast.LENGTH_LONG).show();
            }
        }
    }
}