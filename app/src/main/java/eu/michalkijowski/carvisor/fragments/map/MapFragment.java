package eu.michalkijowski.carvisor.fragments.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.activities.MainActivity;

public class MapFragment extends Fragment {

    public static final int DATEPICKER_FRAGMENT=1;

    String regex = "";
    private ProgressDialog mProgressDialog;
    MapView map = null;
    boolean personChoose = false;
    public static String startTimestamp;
    public static String endTimestamp;
    public static String date;
    public static boolean flag = false;
    public static int selectedId;
    public static String selectedName;
    TextView dateForm;
    TextView selectForm;

    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
    }

    public void changeDate(Date date) {
        Calendar end = Calendar.getInstance();
        end.setTime(date);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        Calendar start = Calendar.getInstance();
        start.setTime(date);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        startTimestamp = String.valueOf(start.getTimeInMillis()/1000);
        endTimestamp = String.valueOf(end.getTimeInMillis()/1000);
    }

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
        final View root = inflater.inflate(R.layout.fragment_map, container, false);
        dateForm = (TextView)root.findViewById(R.id.textView22);
        selectForm = (TextView)root.findViewById(R.id.textView20);
        /*********************
         Map configuration
         ********************/
        Context ctx = root.getContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        map = (MapView) root.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(false);
        map.setMultiTouchControls(true);
        RotationGestureOverlay mRotationGestureOverlay = new RotationGestureOverlay(getContext(), map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);
        /*********************
         Selectors configuration
         ********************/
        ImageView typeSelector = root.findViewById(R.id.imageView4);
        ImageView dateSelector = root.findViewById(R.id.imageView11);
        ImageView devicePersonSelector = root.findViewById(R.id.imageView7);
        typeSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personChoose = !personChoose;
                if (personChoose) {
                    ((TextView)root.findViewById(R.id.textView20)).setText("Wybierz kierowcę");
                    ((ImageView)root.findViewById(R.id.imageView4)).setImageResource(R.drawable.tab2);
                }
                else {
                    ((TextView)root.findViewById(R.id.textView20)).setText("Wybierz pojazd");
                    ((ImageView)root.findViewById(R.id.imageView4)).setImageResource(R.drawable.tab);
                }
                MapFragment.selectedName = null;
                MapFragment.selectedId = 0;
            }
        });
        devicePersonSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (personChoose) {
                    PersonDialog personDialog = new PersonDialog();
                    Bundle bundle = new Bundle();
                    personDialog.show(getFragmentManager(), MapFragment.this, bundle, getContext());
                    new UpdatePerson().execute(personDialog);
                }
                else {
                    DeviceDialog deviceDialog = new DeviceDialog();
                    Bundle bundle = new Bundle();
                    deviceDialog.show(getFragmentManager(), MapFragment.this, bundle, getContext());
                    new UpdateDevice().execute(deviceDialog);
                }
            }
        });
        dateSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new SimpleDateFormat("dd.MM.yyyy").parse(date));
                    DatePicker dialogFragment = new DatePicker(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
                    dialogFragment.show(getParentFragmentManager(), "date");
                    new UpdateDate().execute(dialogFragment);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        Date date = new Date();
        SimpleDateFormat changeFormat = new SimpleDateFormat("dd.MM.yyyy");
        this.date = changeFormat.format(date);
        ((TextView)root.findViewById(R.id.textView22)).setText(this.date);
        /*********************
         Fragment configuration
         ********************/
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(MapFragment.this)
                        .navigate(R.id.action_nav_my_fleet_to_nav_my_fleet,bundle);
            }
        });
        return root;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        new DownloadDataForList().execute();
    }



    private class DownloadDataForList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Void voids) {
            super.onPostExecute(voids);
            mProgressDialog.dismiss();
            IMapController mapController = map.getController();
            mapController.setZoom(16);
            GeoPoint startPoint = new GeoPoint(52.4604487,16.9160836);
            mapController.setCenter(startPoint);
        }
    }

    private class UpdateDate extends AsyncTask<DatePicker, Void, DatePicker> {
        @Override
        protected DatePicker doInBackground(DatePicker... datePickers) {
            while (!MapFragment.flag) {
                try {
                    Thread.sleep( 300 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            MapFragment.flag = false;
            return datePickers[0];
        }

        @Override
        protected void onPostExecute(DatePicker datePicker) {
            super.onPostExecute(datePicker);
            MapFragment.startTimestamp = datePicker.getStartTimestamp();
            MapFragment.endTimestamp = datePicker.getEndTimestamp();
            MapFragment.date = datePicker.getDate();
            dateForm.setText(MapFragment.date);
        }
    }

    private class UpdatePerson extends AsyncTask<PersonDialog, Void, PersonDialog> {
        @Override
        protected PersonDialog doInBackground(PersonDialog... personDialogs) {
            while (!MapFragment.flag) {
                try {
                    Thread.sleep( 300 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            MapFragment.flag = false;
            return personDialogs[0];
        }

        @Override
        protected void onPostExecute(PersonDialog personDialog) {
            super.onPostExecute(personDialog);
            selectForm.setText(MapFragment.selectedName);
        }
    }

    private class UpdateDevice extends AsyncTask<DeviceDialog, Void, DeviceDialog> {
        @Override
        protected DeviceDialog doInBackground(DeviceDialog... deviceDialogs) {
            while (!MapFragment.flag) {
                try {
                    Thread.sleep( 300 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            MapFragment.flag = false;
            return deviceDialogs[0];
        }

        @Override
        protected void onPostExecute(DeviceDialog deviceDialog) {
            super.onPostExecute(deviceDialog);
            selectForm.setText(MapFragment.selectedName);
        }
    }

    /*private class UpdateMap extends AsyncTask<Void, Void, DeviceDialog> {
        @Override
        protected DeviceDialog doInBackground(Void... voids) {

            return deviceDialogs[0];
        }

        @Override
        protected void onPostExecute(DeviceDialog deviceDialog) {
            super.onPostExecute(deviceDialog);
            selectForm.setText(MapFragment.selectedName);
        }
    }*/
}