package eu.michalkijowski.carvisor.fragments.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import eu.michalkijowski.carvisor.data_models.DeviceNamesDTO;
import eu.michalkijowski.carvisor.data_models.UserNamesDTO;
import eu.michalkijowski.carvisor.services.MapService;

public class DeviceDialog extends DialogFragment {

    FragmentManager manager;
    String tag;
    Fragment fragment;
    Bundle bundle;
    Context context;

    public void show(@NonNull FragmentManager manager, Fragment fragment, Bundle bundle, Context context) {
        this.manager = manager;
        this.fragment = fragment;
        this.bundle = bundle;
        this.context = context;
        super.show(manager, tag);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final DeviceNamesDTO[] deviceNamesDTOS = MapService.getDeviceList("$");
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(fragment.getContext(), android.R.layout.select_dialog_singlechoice);
        for (DeviceNamesDTO deviceNamesDTO : deviceNamesDTOS) {
            arrayAdapter.add(deviceNamesDTO.getName());
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int id = 0;
                for (DeviceNamesDTO deviceNamesDTO : deviceNamesDTOS) {
                    if (deviceNamesDTO.getName().equals(arrayAdapter.getItem(which))) {
                        MapFragment.selectedId = deviceNamesDTO.getId();
                        MapFragment.selectedName = deviceNamesDTO.getName();
                        MapFragment.flag = true;
                    }
                }
            }
        });
        return builder.create();
    }

}
