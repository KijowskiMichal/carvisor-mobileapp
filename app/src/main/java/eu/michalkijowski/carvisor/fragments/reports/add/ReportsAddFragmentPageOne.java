package eu.michalkijowski.carvisor.fragments.reports.add;

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
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserDTO;
import eu.michalkijowski.carvisor.data_models.UserNamesDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.list.RowActionDialog;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.ReportsService;
import eu.michalkijowski.carvisor.services.UsersService;

public class ReportsAddFragmentPageOne extends Fragment {

    String regex = "";
    private ProgressDialog mProgressDialog;
    ListView listView;
    FloatingActionButton fab;
    HashSet<Integer> selectedIds = new HashSet<Integer>();
    UserNamesDTO[] userNamesDTOS;

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
        View root = inflater.inflate(R.layout.fragment_reports_add_page_one, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
        /********************************
         * Floating action button
         *******************************/
        fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedIds.size()>0) {
                    Bundle bundle = getArguments();
                    bundle.putIntArray("selected", selectedIds.stream().mapToInt(Integer::intValue).toArray());
                    NavHostFragment.findNavController(ReportsAddFragmentPageOne.this)
                            .navigate(R.id.action_nav_report_add_page_one_to_nav_reports_add_page_two,bundle);
                }
                else {
                    Toast.makeText(getContext(), "Nie zaznaczono żadnego kierowcy.", Toast.LENGTH_LONG).show();
                }
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ReportsAddFragmentPageOne.this)
                        .navigate(R.id.action_nav_report_add_page_one_to_nav_reports_add_page_zero,bundle);
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
            if (userNamesDTOS==null) userNamesDTOS = ReportsService.getUserList(regex);
            List<HashMap<String, String>> list = new ArrayList<>();
            for (UserNamesDTO userNamesDTO : userNamesDTOS) {
                HashMap item = new HashMap<String, String>();
                item.put("userId", String.valueOf(userNamesDTO.getId()));
                item.put("name", userNamesDTO.getName());
                item.put("switch", selectedIds.contains(userNamesDTO.getId()) ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);
                //image
                try {
                    byte[] bytes = Base64.decode(userNamesDTO.getImage().replace("data:image/png;base64,", ""), Base64.DEFAULT);
                    Bitmap bitmap = ImageService.getCircleImage(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                    item.put("userImage", new BitmapDrawable(getResources(), bitmap));
                }
                catch (IllegalArgumentException | NullPointerException e) {
                    e.printStackTrace();
                }
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_reports_add_page_one_row,
                    new String[]{"userId", "name", "switch", "userImage"},
                    new int[]{R.id.userId, R.id.name, R.id.switch1, R.id.userImage});
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
            if (userNamesDTOS==null) mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(final SimpleAdapter simpleAdapter) {
            super.onPostExecute(simpleAdapter);
            listView.setAdapter(simpleAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    TextView idTextView = (TextView) view.findViewById(R.id.userId);
                    int identifier = Integer.valueOf(idTextView.getText().toString().trim());
                    if (selectedIds.contains(identifier)) {
                        selectedIds.remove(identifier);
                        new DownloadDataForList().execute();
                    }
                    else {
                        selectedIds.add(identifier);
                        new DownloadDataForList().execute();
                    }
                }

            });
            mProgressDialog.dismiss();
        }
    }
}