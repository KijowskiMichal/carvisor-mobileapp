package eu.michalkijowski.carvisor.fragments.zones.edit;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.util.ArrayList;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserDataDTO;
import eu.michalkijowski.carvisor.data_models.ZonesAddDTO;
import eu.michalkijowski.carvisor.data_models.ZonesDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.edit.MyFleetEditFragment;
import eu.michalkijowski.carvisor.fragments.zones.utils.CircleOverlay;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;
import eu.michalkijowski.carvisor.services.ZonesService;

public class ZonesEditFragment extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;
    MapView map = null;
    String longitude;
    String latitude;
    Overlay touchOverlay;
    NumberPicker np;
    int id;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public ZonesDTO register(View view) {
        try {
            String name = ((EditText)getView().findViewById(R.id.editTextTextPersonName14)).getText().toString();

            if (name.equals("")) return null;
            if (latitude==null) return null;

            ZonesDTO zonesDTO = new ZonesDTO();
            zonesDTO.setName(name);
            zonesDTO.setPointX(latitude);
            zonesDTO.setPointY(longitude);
            zonesDTO.setId(id);
            zonesDTO.setRadius(np.getValue()*100+100);

            return zonesDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        this.id = getArguments().getInt("id");
        new DownloadData().execute(this.id);
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_zones_add, container, false);
        np = (NumberPicker) root.findViewById(R.id.numberPicker);
        ArrayList<String> values = new ArrayList<>();
        for (int i=100; i<=100000; i+=100)
            values.add(String.valueOf(i));
        np.setMinValue(0);
        np.setMaxValue(values.size() - 1);
        np.setDisplayedValues(values.stream().toArray(String[]::new));
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (latitude!=null) {
                    drawCircle(null, map, np.getValue()*100+100);
                }
            }
        });
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
        map.getController().setCenter(new GeoPoint(52.4604487,16.9160836));
        map.getController().setZoom(16);
        touchOverlay = new Overlay(getActivity()){
            ItemizedIconOverlay<OverlayItem> anotherItemizedIconOverlay = null;
            @Override
            public void draw(Canvas arg0, MapView arg1, boolean arg2) {

            }
            @Override
            public boolean onSingleTapConfirmed(final MotionEvent e, final MapView mapView) {
                drawCircle(e, mapView, np.getValue()*100+100);

                return true;
            }
        };
        map.getOverlays().add(touchOverlay);
        /********************************
         * Image selector
         *******************************/
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ZonesEditFragment.this)
                        .navigate(R.id.action_nav_zones_edit_to_nav_zones,bundle);
            }
        });
        /********************************
         * Floating action button
         *******************************/
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZonesDTO zonesDTO = register(v);
                if (zonesDTO!=null) {
                    new EditZone().execute(zonesDTO);
                }
                else {
                    Toast.makeText(getContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
                }
            }
        });
        return root;
    }

    private void drawCircle(MotionEvent e, MapView mapView, int radius) {
        if (e != null) {
            Projection proj = mapView.getProjection();
            GeoPoint loc = (GeoPoint) proj.fromPixels((int)e.getX(), (int)e.getY());
            longitude = Double.toString(((double)loc.getLongitudeE6())/1000000);
            latitude = Double.toString(((double)loc.getLatitudeE6())/1000000);
        }

        map.getOverlays().clear();
        map.getOverlays().add(touchOverlay);
        map.getOverlays().add(new CircleOverlay(getActivity(), Double.valueOf(latitude), Double.valueOf(longitude), radius));
        map.invalidate();
    }

    private class EditZone extends AsyncTask<ZonesDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ZonesDTO... zonesDTOS) {
            return ZonesService.editZone(zonesDTOS[0], id);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            super.onPostExecute(bool);
            mProgressDialog.dismiss();
            if (bool) {
                Toast.makeText(getContext(), "Poprawnie zedytowano strefę.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ZonesEditFragment.this)
                        .navigate(R.id.action_nav_zones_edit_to_nav_zones, bundle);
            }
            else {
                Toast.makeText(getContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class DownloadData extends AsyncTask<Integer, Void, ZonesDTO> {
        @Override
        protected ZonesDTO doInBackground(Integer... integers) {
            return ZonesService.getZone(integers[0]);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(ZonesDTO zonesDTO) {
            super.onPostExecute(zonesDTO);

            if (zonesDTO==null) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "Nie masz uprawnień do edycji tego użytkownika.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ZonesEditFragment.this)
                        .navigate(R.id.action_nav_zones_edit_to_nav_zones,bundle);
                mProgressDialog.dismiss();
                return;
            }

            ((EditText)getView().findViewById(R.id.editTextTextPersonName14)).setText(zonesDTO.getName());
            np.setValue((zonesDTO.getRadius()-100)/100);
            longitude = zonesDTO.getPointY();
            latitude = zonesDTO.getPointX();
            drawCircle(null, map, np.getValue()*100+100);
            map.getController().setCenter(new GeoPoint(Double.valueOf(latitude), Double.valueOf(longitude)));
            map.getController().setZoom(getZoomLevel(zonesDTO.getRadius()));

            mProgressDialog.dismiss();
        }
    }

    public static double getZoomLevel(int radius) {
        double scale = radius/5.0;
        return (16 - Math.log(scale)) / Math.log(2);
    }
}