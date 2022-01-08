package eu.michalkijowski.carvisor.fragments.map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
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
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.DeviceNamesDTO;
import eu.michalkijowski.carvisor.data_models.MapDeviceRowDTO;
import eu.michalkijowski.carvisor.data_models.MapRowDTO;
import eu.michalkijowski.carvisor.data_models.MapWrapperDTO;
import eu.michalkijowski.carvisor.data_models.UserNamesDTO;
import eu.michalkijowski.carvisor.services.AuthorizationService;
import eu.michalkijowski.carvisor.services.MapService;

public class MapStandardUserFragment extends Fragment {

    public static final int DATEPICKER_FRAGMENT=1;

    String regex = "";
    private ProgressDialog mProgressDialog;
    MapView map = null;
    public static String timestamp;
    public static String date;
    public static boolean flag = false;
    public static int selectedId = AuthorizationService.authorizationStatus.getId();
    TextView dateForm;

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
        final View root = inflater.inflate(R.layout.fragment_map_standard_user, container, false);
        dateForm = (TextView)root.findViewById(R.id.textView22);
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
        ImageView dateSelector = root.findViewById(R.id.imageView11);
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
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.timestamp = String.valueOf(calendar.getTimeInMillis()/1000);
        ((TextView)root.findViewById(R.id.textView22)).setText(this.date);
        /*********************
         Fragment configuration
         ********************/
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(MapStandardUserFragment.this)
                        .navigate(R.id.action_nav_map_standard_user_to_nav_my_map_standard_user,bundle);
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
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*********************
                         * Pokaż na mapie
                         ********************/
                        new UpdateMap().execute();
                    }
                });
                }
            }, 0, 3000);
        }
    }

    private class UpdateDate extends AsyncTask<DatePicker, Void, DatePicker> {
        @Override
        protected DatePicker doInBackground(DatePicker... datePickers) {
            while (!MapStandardUserFragment.flag) {
                try {
                    Thread.sleep( 300 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            MapStandardUserFragment.flag = false;
            return datePickers[0];
        }

        @Override
        protected void onPostExecute(DatePicker datePicker) {
            super.onPostExecute(datePicker);
            MapStandardUserFragment.timestamp = datePicker.getTimestamp();
            MapStandardUserFragment.date = datePicker.getDate();
            dateForm.setText(MapStandardUserFragment.date);
            if (selectedId!=0) new UpdateMap().execute();
        }
    }

    private class UpdateMap extends AsyncTask<Void, Void, MapWrapperDTO> {
        @Override
        protected MapWrapperDTO doInBackground(Void... voids) {
            return MapService.getMapFromUser(selectedId, Long.valueOf(timestamp));
        }

        @Override
        protected void onPostExecute(MapWrapperDTO mapWrapperDTO) {
            super.onPostExecute(mapWrapperDTO);
            map.getOverlays().clear();
            map.invalidate();
            if (mapWrapperDTO.getPoints().length>0) {
                Polyline polyline = new Polyline();
                polyline.setColor(Color.BLUE);
                polyline.setWidth(8);
                map.getOverlays().add(polyline);

                ArrayList<GeoPoint> pathPoints = new ArrayList<>();
                for (MapRowDTO mapRowDTO : mapWrapperDTO.getPoints()) {
                    pathPoints.add(new GeoPoint(mapRowDTO.getGpsX(), mapRowDTO.getGpsY()));
                }
                polyline.setPoints(pathPoints);
                for (MapRowDTO mapRowDTO : mapWrapperDTO.getPoints()) {
                    GeoPoint startPoint = new GeoPoint(mapRowDTO.getGpsX(), mapRowDTO.getGpsY());
                    Marker marker = new Marker(map);
                    marker.setIcon(ContextCompat.getDrawable(getContext(), R.mipmap.point));
                    marker.setPosition(startPoint);
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER);
                    marker.setTitle("Obroty: "+mapRowDTO.getRpm()+"\nPrędkość: "+mapRowDTO.getSpeed()+"km/h \nCzas: "+(new SimpleDateFormat("hh:mm").format(new Date(mapRowDTO.getTime()))));
                    map.getOverlays().add(marker);
                }
                for (MapDeviceRowDTO mapDeviceRowDTO : mapWrapperDTO.getStartPoints()) {
                    GeoPoint startPoint = new GeoPoint(mapDeviceRowDTO.getGpsX(), mapDeviceRowDTO.getGpsY());
                    Marker marker = new Marker(map);
                    marker.setIcon(ContextCompat.getDrawable(getContext(), R.mipmap.green));
                    marker.setPosition(startPoint);
                    marker.setAnchor(Marker.ANCHOR_RIGHT, Marker.ANCHOR_BOTTOM);
                    marker.setTitle("Początek trasy\n"+(mapDeviceRowDTO.isPrivateTrack() ? "Trasa prywatna" : "Trasa firmowa")+"\nPojazd: "+mapDeviceRowDTO.getVehicle()+"\nObroty: "+mapDeviceRowDTO.getRpm()+"\nPrędkość: "+mapDeviceRowDTO.getSpeed()+"km/h \nCzas: "+(new SimpleDateFormat("hh:mm").format(new Date(mapDeviceRowDTO.getTime()))));
                    map.getOverlays().add(marker);
                }
                for (MapDeviceRowDTO mapDeviceRowDTO : mapWrapperDTO.getEndPoints()) {
                    GeoPoint startPoint = new GeoPoint(mapDeviceRowDTO.getGpsX(), mapDeviceRowDTO.getGpsY());
                    Marker marker = new Marker(map);
                    marker.setIcon(ContextCompat.getDrawable(getContext(), R.mipmap.red));
                    marker.setPosition(startPoint);
                    marker.setAnchor(Marker.ANCHOR_LEFT, Marker.ANCHOR_BOTTOM);
                    marker.setTitle("Koniec trasy\n"+(mapDeviceRowDTO.isPrivateTrack() ? "Trasa prywatna" : "Trasa firmowa")+"\nPojazd: "+mapDeviceRowDTO.getVehicle()+"\nObroty: "+mapDeviceRowDTO.getRpm()+"\nPrędkość: "+mapDeviceRowDTO.getSpeed()+"km/h \nCzas: "+(new SimpleDateFormat("hh:mm").format(new Date(mapDeviceRowDTO.getTime()))));
                    map.getOverlays().add(marker);
                    map.getController().setCenter(startPoint);
                }
            }
        }
    }
}