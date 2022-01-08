package eu.michalkijowski.carvisor.fragments.zones.list;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.fragment.NavHostFragment;

import eu.michalkijowski.carvisor.R;

public class RowActionDialog extends DialogFragment {

    FragmentManager manager;
    String tag;
    String name;
    Fragment fragment;
    Bundle bundle;
    Context context;

    public void show(@NonNull FragmentManager manager, @Nullable String tag, String name, Fragment fragment, Bundle bundle, Context context) {
        this.manager = manager;
        this.tag = tag;
        this.fragment = fragment;
        this.bundle = bundle;
        this.name = name;
        this.context = context;
        super.show(manager, tag);
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(name)
                .setItems(R.array.zones_row_action, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which==0) {
                            bundle.putInt("id", Integer.valueOf(tag));
                            NavHostFragment.findNavController(fragment)
                                    .navigate(R.id.action_nav_my_fleet_to_nav_my_fleet_edit,bundle);
                        }
                        else if (which==1) {
                            new RowDeleteDialog().show(manager, tag, name, fragment, context);
                        }
                        else if (which==2) {
                            bundle.putInt("mapUserID", Integer.valueOf(tag));
                            NavHostFragment.findNavController(fragment)
                                    .navigate(R.id.action_nav_my_fleet_to_nav_map,bundle);
                        }
                    }
                });
        return builder.create();
    }

}
