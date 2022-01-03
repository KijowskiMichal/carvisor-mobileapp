package eu.michalkijowski.carvisor.fragments.myFleet.nfctag;

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
import java.util.Timer;
import java.util.TimerTask;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.HomeActivity;
import eu.michalkijowski.carvisor.data_models.NfcTagDTO;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class NfcTagFragment extends Fragment {
    int id;
    String tag;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity) getActivity()).enableNfcSearching();
    }

    @Override
    public void onPause() {
        super.onPause();
        ((HomeActivity) getActivity()).disableNfcSearching();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        this.id = bundle.getInt("id");
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_my_fleet_nfc_tag, container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(NfcTagFragment.this)
                        .navigate(R.id.action_nav_my_fleet_to_nav_my_fleet_nfc_tag,bundle);
            }
        });
        HomeActivity.listOfFindedNFC.clear();
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (HomeActivity.listOfFindedNFC.size()==1) {
                            tag = HomeActivity.listOfFindedNFC.get(0);
                            HomeActivity.listOfFindedNFC.clear();
                            new SetNfcTag().execute();
                        }
                    }
                });
            }
        }, 0, 1000);
        return root;
    }

    private class SetNfcTag extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            NfcTagDTO nfcTagDTO = new NfcTagDTO();
            nfcTagDTO.setTag(tag);
            UsersService.setNfcTag(nfcTagDTO, id);
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
            Toast.makeText(getContext(), "Poprawnie przypisano NFC tag do użytkownika.", Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            NavHostFragment.findNavController(NfcTagFragment.this)
                    .navigate(R.id.action_nav_my_fleet_nfc_tag_to_nav_my_fleet,bundle);
        }
    }
}