package eu.michalkijowski.carvisor.fragments.summary;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
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
import java.util.TimeZone;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.EcopointDTO;
import eu.michalkijowski.carvisor.data_models.EcopointsDTO;
import eu.michalkijowski.carvisor.data_models.OffenceDayDTO;
import eu.michalkijowski.carvisor.data_models.ReverseGeocodingDTO;
import eu.michalkijowski.carvisor.data_models.SummaryDTO;
import eu.michalkijowski.carvisor.services.EcopointsService;
import eu.michalkijowski.carvisor.services.MapService;
import eu.michalkijowski.carvisor.services.ReverseGeocodingService;
import eu.michalkijowski.carvisor.services.SummaryService;

public class SummaryFragment extends Fragment {

    String regex = "";
    private ProgressDialog mProgressDialog;
    ListView listView;
    FloatingActionButton fab;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.home, menu);
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);
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
        View root = inflater.inflate(R.layout.fragment_summary_list, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(SummaryFragment.this)
                        .navigate(R.id.action_nav_summary_to_nav_summary,bundle);
            }
        });
        return root;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        new DownloadDataForList().execute();
    }



    private class DownloadDataForList extends AsyncTask<Void, Void, Pair<SimpleAdapter, SummaryDTO>> {
        @Override
        protected Pair<SimpleAdapter, SummaryDTO> doInBackground(Void... voids) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            /********************************
             * ListView
             *******************************/
            SummaryDTO summaryDTO = SummaryService.getSummaryData();
            List<HashMap<String, String>> list = new ArrayList<>();
            for (OffenceDayDTO offenceDayDTO : summaryDTO.getListOfTracks()) {
                HashMap item = new HashMap<String, String>();
                item.put("date", dateFormat.format(new Date(offenceDayDTO.getDate()*1000)));
                String[] locationFrom = offenceDayDTO.getLocationFrom().split(";");
                ReverseGeocodingDTO reverseGeocodingDTOFrom = ReverseGeocodingService.getReverseGeocoding(Double.parseDouble(locationFrom[0]), Double.parseDouble(locationFrom[1]));
                item.put("locationFrom", reverseGeocodingDTOFrom.getAddress());
                String[] locationTo = offenceDayDTO.getLocationTo().split(";");
                ReverseGeocodingDTO reverseGeocodingDTOTo = ReverseGeocodingService.getReverseGeocoding(Double.parseDouble(locationTo[0]), Double.parseDouble(locationTo[1]));
                item.put("locationTo", reverseGeocodingDTOTo.getAddress());
                item.put("offences", String.valueOf(offenceDayDTO.getListOfOffencess().length));
                item.put("ecopoints", offenceDayDTO.getEcoPoints());
                item.put("safetypoints", offenceDayDTO.getSafetyPoints());
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_summary_list_row,
                    new String[]{"date", "locationFrom", "locationTo", "offences", "ecopoints", "safetypoints"},
                    new int[]{R.id.textView23, R.id.textView24, R.id.textView27, R.id.tracks, R.id.rate, R.id.rate2});
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
            return new Pair<>(simpleAdapter, summaryDTO);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getContext(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Pair<SimpleAdapter, SummaryDTO> pair) {
            super.onPostExecute(pair);
            listView.setAdapter(pair.first);
            ((TextView)getView().findViewById(R.id.textView26)).setText(pair.second.getName());
            ((TextView)getView().findViewById(R.id.rate)).setText(String.format("%.1f", pair.second.getEcoPoints()));
            ((TextView)getView().findViewById(R.id.ratesafety)).setText(String.format("%.1f", pair.second.getSafetyPoints()));
            mProgressDialog.dismiss();
        }
    }
}