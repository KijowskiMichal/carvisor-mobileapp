package eu.michalkijowski.carvisor.fragments.zones.list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserDTO;
import eu.michalkijowski.carvisor.data_models.ZonesDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.list.RowActionDialog;
import eu.michalkijowski.carvisor.services.AuthorizationService;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.ReverseGeocodingService;
import eu.michalkijowski.carvisor.services.UsersService;
import eu.michalkijowski.carvisor.services.ZonesService;

public class ZoneListFragment extends Fragment {

    String regex = "";
    private ProgressDialog mProgressDialog;
    ListView listView;
    FloatingActionButton fab;



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
        View root = inflater.inflate(R.layout.fragment_zones_list, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
        /********************************
         * Floating action button
         *******************************/
        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ZoneListFragment.this)
                        .navigate(R.id.action_nav_my_fleet_to_nav_my_fleet_add,bundle);
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ZoneListFragment.this)
                        .navigate(R.id.action_nav_zones_to_nav_zones,bundle);
            }
        });
        return root;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        new DownloadDataForList().execute();
    }



    private class DownloadDataForList extends AsyncTask<Void, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(Void... voids) {
            /********************************
             * ListView
             *******************************/
            ZonesDTO[] zonesDTOS = ZonesService.getZones(regex);
            List<HashMap<String, String>> list = new ArrayList<>();
            for (ZonesDTO zonesDTO : zonesDTOS) {
                HashMap item = new HashMap<String, String>();
                item.put("userId", String.valueOf(zonesDTO.getId()));
                item.put("name", zonesDTO.getName());
                item.put("licensePlate", ReverseGeocodingService.getReverseGeocoding(Double.parseDouble(zonesDTO.getPointX()), Double.parseDouble(zonesDTO.getPointY())).getAddress());
                item.put("distance", zonesDTO.getRadius() + " m");
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_zones_list_row,
                    new String[]{"userId", "name", "licensePlate", "distance"},
                    new int[]{R.id.userId, R.id.name, R.id.licensePlate, R.id.distance});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    if (view instanceof ImageView && data instanceof Drawable) {
                        ImageView iv = (ImageView) view;
                        iv.setImageDrawable((Drawable) data);
                        return true;
                    } else
                        return false;
                }
            });
            return simpleAdapter;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(SimpleAdapter simpleAdapter) {
            super.onPostExecute(simpleAdapter);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    TextView idTextView = (TextView) view.findViewById(R.id.userId);
                    int identifier = Integer.valueOf(idTextView.getText().toString().trim());
                    TextView nameTextView = (TextView) view.findViewById(R.id.name);
                    String name = nameTextView.getText().toString().trim();
                    RowActionDialog rowActionDialog = new RowActionDialog();
                    Bundle bundle = new Bundle();
                    rowActionDialog.show(getFragmentManager(), String.valueOf(identifier), name, ZoneListFragment.this, bundle, getContext());
                }

            });
            mProgressDialog.dismiss();
        }
    }
}