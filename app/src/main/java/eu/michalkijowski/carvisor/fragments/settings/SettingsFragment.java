package eu.michalkijowski.carvisor.fragments.settings;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.FileNotFoundException;
import java.io.InputStream;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.converters.GlobalConfigurationConverter;
import eu.michalkijowski.carvisor.data_models.GlobalSettingsDTO;
import eu.michalkijowski.carvisor.data_models.SetGlobalSettingsDTO;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.edit.MyFleetEditFragment;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.SettingsService;
import eu.michalkijowski.carvisor.services.UsersService;

public class SettingsFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    ArrayAdapter<CharSequence> adapter;
    ArrayAdapter<CharSequence> adapter2;
    ArrayAdapter<CharSequence> adapter3;
    Spinner spinner;
    Spinner spinner2;
    Spinner spinner3;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public SetGlobalSettingsDTO setSettings(View view) {
        try {
            String history = ((Spinner)getView().findViewById(R.id.spinner)).getSelectedItem().toString();
            String send = ((Spinner)getView().findViewById(R.id.spinner2)).getSelectedItem().toString();
            String location = ((Spinner)getView().findViewById(R.id.spinner3)).getSelectedItem().toString();

            if (history.equals("")) return null;
            if (send.equals("")) return null;
            if (location.equals("")) return null;

            SetGlobalSettingsDTO setGlobalSettingsDTO = new SetGlobalSettingsDTO();
            setGlobalSettingsDTO.setGetLocationInterval(String.valueOf(GlobalConfigurationConverter.getIntFromLocationInterval(location)));
            setGlobalSettingsDTO.setHistoryTimeout(String.valueOf(GlobalConfigurationConverter.getIntFromHistoryInterval(history)));
            setGlobalSettingsDTO.setSendInterval(String.valueOf(GlobalConfigurationConverter.getIntFromSendInterval(send)));

            return setGlobalSettingsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UserPasswordDTO changePassword(View view) {
        try {
            String password1 = ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).getText().toString();
            String password2 = ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).getText().toString();

            if (password1.equals("")) return null;
            if (password2.equals("")) return null;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_settings, container, false);
        Button settingsButton = root.findViewById(R.id.button3);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetGlobalSettingsDTO setGlobalSettingsDTO = setSettings(view);
                if (setGlobalSettingsDTO!=null) {
                    new SetConfiguration().execute(setGlobalSettingsDTO);
                }
                else {
                    Toast.makeText(getContext(), "Nie wszystkie pola wypełniono poprawnie.", Toast.LENGTH_LONG).show();
                }
            }
        });
        Button changePasswordSettings = root.findViewById(R.id.button2);
        changePasswordSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserPasswordDTO userPasswordDTO = changePassword(view);
                if (userPasswordDTO!=null) {
                    new EditUserPassword().execute(userPasswordDTO);
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
                NavHostFragment.findNavController(SettingsFragment.this)
                        .navigate(R.id.action_nav_settings_to_nav_settings,bundle);
            }
        });
        /********************************
         * Select (Spinners)
         *******************************/
        spinner = (Spinner) root.findViewById(R.id.spinner);
        adapter = ArrayAdapter.createFromResource(root.getContext(),R.array.history_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner2 = (Spinner) root.findViewById(R.id.spinner2);
        adapter2 = ArrayAdapter.createFromResource(root.getContext(),R.array.send_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter2);

        spinner3 = (Spinner) root.findViewById(R.id.spinner3);
        adapter3 = ArrayAdapter.createFromResource(root.getContext(),R.array.location_interval, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter3);

        new GetData().execute();

        return root;
    }

    private class GetData extends AsyncTask<Void, Void, GlobalSettingsDTO> {
        @Override
        protected GlobalSettingsDTO doInBackground(Void... params) {
            return SettingsService.getGlobalConfiguration();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(GlobalSettingsDTO globalSettingsDTO) {
            super.onPostExecute(globalSettingsDTO);
            mProgressDialog.dismiss();
            spinner.setSelection(adapter.getPosition(GlobalConfigurationConverter.getStringFromHistoryInterval(globalSettingsDTO.getHistoryTimeout())));
            spinner2.setSelection(adapter2.getPosition(GlobalConfigurationConverter.getStringFromSendInterval(globalSettingsDTO.getSendInterval())));
            spinner3.setSelection(adapter3.getPosition(GlobalConfigurationConverter.getStringFromLocationInterval(globalSettingsDTO.getLocationInterval())));
        }
    }

    private class SetConfiguration extends AsyncTask<SetGlobalSettingsDTO, Void, Void> {
        @Override
        protected Void doInBackground(SetGlobalSettingsDTO... setGlobalSettingsDTOS) {
            SettingsService.setGlobalConfiguration(setGlobalSettingsDTOS[0]);
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
            Toast.makeText(getContext(), "Poprawnie zedytowano ustawienia.", Toast.LENGTH_LONG).show();
        }
    }

    private class EditUserPassword extends AsyncTask<UserPasswordDTO, Void, Void> {
        @Override
        protected Void doInBackground(UserPasswordDTO... userDataDTO) {
            UsersService.editUserPassword(userDataDTO[0]);
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
            ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).setText("");
            ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).setText("");
            Toast.makeText(getContext(), "Poprawnie zedytowano hasło.", Toast.LENGTH_LONG).show();
        }
    }
}