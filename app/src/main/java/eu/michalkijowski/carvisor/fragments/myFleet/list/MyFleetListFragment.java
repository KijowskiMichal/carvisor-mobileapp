package eu.michalkijowski.carvisor.fragments.myFleet.list;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Base64;
import android.view.ContextMenu;
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
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.HomeActivity;
import eu.michalkijowski.carvisor.data_models.UserDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.add.MyFleetAddFragment;
import eu.michalkijowski.carvisor.fragments.settings.SettingsFragment;
import eu.michalkijowski.carvisor.services.AuthorizationService;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;

public class MyFleetListFragment extends Fragment {

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
        View root = inflater.inflate(R.layout.fragment_my_fleet_list, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
        /********************************
         * Floating action button
         *******************************/
        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(MyFleetListFragment.this)
                        .navigate(R.id.action_nav_my_fleet_to_nav_my_fleet_add,bundle);
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(MyFleetListFragment.this)
                        .navigate(R.id.action_nav_my_fleet_to_nav_my_fleet,bundle);
            }
        });
        return root;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        if (AuthorizationService.authorizationStatus.getRbac().equals("STANDARD_USER")) {
            Bundle bundle = new Bundle();
            NavHostFragment.findNavController(MyFleetListFragment.this)
                    .navigate(R.id.action_nav_my_fleet_to_nav_summary,bundle);
        }
        else {
            new DownloadDataForList().execute();
        }
    }



    private class DownloadDataForList extends AsyncTask<Void, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(Void... voids) {
            /********************************
             * ListView
             *******************************/
            UserDTO[] usersDTO = UsersService.getUsersList(regex).getListOfUsers();
            List<HashMap<String, String>> list = new ArrayList<>();
            for (UserDTO userDTO : usersDTO) {
                HashMap item = new HashMap<String, String>();
                item.put("userId", String.valueOf(userDTO.getId()));
                item.put("name", userDTO.getName() + " " + userDTO.getSurname());
                item.put("licensePlate", userDTO.getLicensePlate().equals("-1") ? "---" : userDTO.getLicensePlate());
                item.put("active", userDTO.getStatus());
                item.put("distance", userDTO.getDistance() + " km");
                item.put("time", userDTO.getStartTime()!=-1 ? ((new SimpleDateFormat("HH:mm").format(new Date(userDTO.getStartTime())))+(userDTO.getFinishTime()!=-1 ? (" - "+(new SimpleDateFormat("HH:mm").format(new Date(userDTO.getFinishTime())))) : " - teraz")) : "------");
                //image
                try {
                    byte[] bytes = Base64.decode(userDTO.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
                    Bitmap bitmap = ImageService.getCircleImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    item.put("userImage", new BitmapDrawable(getResources(), bitmap));
                }
                catch (IllegalArgumentException | NullPointerException e) {
                    e.printStackTrace();
                }
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_my_fleet_list_row,
                    new String[]{"userId", "name", "licensePlate", "active", "distance", "time", "userImage"},
                    new int[]{R.id.userId, R.id.name, R.id.licensePlate, R.id.active, R.id.distance, R.id.time, R.id.userImage});
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
                    rowActionDialog.show(getFragmentManager(), String.valueOf(identifier), name, MyFleetListFragment.this, bundle, getContext());
                }

            });
            mProgressDialog.dismiss();
        }
    }
}