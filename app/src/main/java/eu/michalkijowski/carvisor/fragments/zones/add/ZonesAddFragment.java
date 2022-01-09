package eu.michalkijowski.carvisor.fragments.zones.add;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.UserAddDTO;
import eu.michalkijowski.carvisor.data_models.ZonesAddDTO;
import eu.michalkijowski.carvisor.fragments.myFleet.list.MyFleetListFragment;
import eu.michalkijowski.carvisor.fragments.zones.utils.CircleOverlay;
import eu.michalkijowski.carvisor.services.ImageService;
import eu.michalkijowski.carvisor.services.UsersService;
import eu.michalkijowski.carvisor.services.ZonesService;

public class ZonesAddFragment extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;
    MapView map = null;
    String longitude;
    String latitude;
    Overlay touchOverlay;
    NumberPicker np;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public ZonesAddDTO register(View view) {
        try {
            String name = ((EditText)getView().findViewById(R.id.editTextTextPersonName14)).getText().toString();

            if (name.equals("")) return null;
            if (latitude==null) return null;

            ZonesAddDTO zonesAddDTO = new ZonesAddDTO();
            zonesAddDTO.setName(name);
            zonesAddDTO.setPointX(latitude);
            zonesAddDTO.setPointY(longitude);
            zonesAddDTO.setRadius(np.getValue()*100+100);

            return zonesAddDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
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
                NavHostFragment.findNavController(ZonesAddFragment.this)
                        .navigate(R.id.action_nav_zones_to_nav_zones,bundle);
            }
        });
        /********************************
         * Floating action button
         *******************************/
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ZonesAddDTO zonesAddDTO = register(v);
                if (zonesAddDTO!=null) {
                    new PostNewZone().execute(zonesAddDTO);
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

    private class PostNewZone extends AsyncTask<ZonesAddDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(ZonesAddDTO... zonesAddDTOS) {
            return ZonesService.addZone(zonesAddDTOS[0]);
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
                Toast.makeText(getContext(), "Poprawnie dodano strefę.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(ZonesAddFragment.this)
                        .navigate(R.id.action_nav_zones_add_to_nav_zones, bundle);
            }
            else {
                Toast.makeText(getContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
            }
        }
    }
}