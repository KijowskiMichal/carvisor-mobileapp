package eu.michalkijowski.carvisor.fragments.devices.add;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.fragments.devices.list.DevicesListFragment;
import eu.michalkijowski.carvisor.fragments.myFleet.add.MyFleetAddFragment;
import eu.michalkijowski.carvisor.fragments.myFleet.list.RowActionDialog;

public class DevicesAddBluetoothListFragment extends Fragment {

    String regex = "";
    private ProgressDialog mProgressDialog;
    ListView listView;
    private BluetoothAdapter BTAdapter;
    Set<BluetoothDevice> pairedDevices;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                regex = query;
                onAttach(getActivity());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")){
                    this.onQueryTextSubmit("");
                }
                return true;
            }
        });
        super.onCreateOptionsMenu(menu,inflater);
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_devices_add_bluetooth_list, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
        BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
        }
        new DownloadDataForList().execute();
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(DevicesAddBluetoothListFragment.this)
                        .navigate(R.id.action_nav_devices_add_to_nav_devices,bundle);
            }
        });
        return root;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
    }



    private class DownloadDataForList extends AsyncTask<Void, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(Void... params) {
            /********************************
             * ListView
             *******************************/
            if (pairedDevices.size()==0) {
                return null;
            }
            List<HashMap<String, String>> list = new ArrayList<>();
            for (BluetoothDevice bt : pairedDevices) {
                HashMap item = new HashMap<String, String>();
                item.put("row1", bt.getName());
                item.put("row2", bt.getAddress());
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_devices_add_bluetooth_list_row,
                    new String[]{"row1", "row2"},
                    new int[]{R.id.row1, R.id.row2});
            return simpleAdapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
            pairedDevices =  BTAdapter.getBondedDevices();
        }

        @Override
        protected void onPostExecute(SimpleAdapter simpleAdapter) {
            super.onPostExecute(simpleAdapter);
            if (simpleAdapter==null) {
                Toast.makeText(getActivity(), "Jeśli nie ma tutaj Twojego urządzenia, sparuj je najpierw.", Toast.LENGTH_LONG).show();
            }
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    TextView address = (TextView) view.findViewById(R.id.row2);
                    String addr = address.getText().toString().trim();
                    Bundle bundle = new Bundle();
                    bundle.putString("address", addr);
                    NavHostFragment.findNavController(DevicesAddBluetoothListFragment.this)
                            .navigate(R.id.action_nav_devices_add_to_nav_devices_add_form,bundle);
                }

            });
            mProgressDialog.dismiss();
        }
    }
}