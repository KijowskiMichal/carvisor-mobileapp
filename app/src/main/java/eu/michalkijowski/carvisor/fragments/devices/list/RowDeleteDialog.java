package eu.michalkijowski.carvisor.fragments.devices.list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.services.DevicesService;
import eu.michalkijowski.carvisor.services.UsersService;

public class RowDeleteDialog extends DialogFragment {

    String tag;
    String name;
    private ProgressDialog mProgressDialog;
    Fragment fragment;
    Context context;

    public void show(@NonNull FragmentManager manager, @Nullable String tag, String name, Fragment fragment, Context context) {
        this.tag = tag;
        this.name = name;
        this.fragment = fragment;
        this.context = context;
        super.show(manager, tag);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_add)
                .setTitle("Czy chcesz usunąć pojazd "+name+"?")
                .setPositiveButton(R.string.remove_device_ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                new DeleteDevice().execute();
                            }
                        }
                )
                .setNegativeButton(R.string.remove_user_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //
                            }
                        }
                )
                .create();
    }

    private class DeleteDevice extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            DevicesService.deleteDevice(Integer.valueOf(tag));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.deleting_device), true, false);
        }

        @Override
        protected void onPostExecute(Void params) {
            super.onPostExecute(params);
            mProgressDialog.dismiss();
            Toast.makeText(context, "Poprawnie usunięto pojazd.", Toast.LENGTH_LONG).show();
            Bundle bundle = new Bundle();
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_nav_devices_to_nav_devices,bundle);
        }
    }

}
