package eu.michalkijowski.carvisor.fragments.notification;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.ErrorDTO;
import eu.michalkijowski.carvisor.data_models.ErrorsDTO;
import eu.michalkijowski.carvisor.data_models.NotificationDTO;
import eu.michalkijowski.carvisor.data_models.NotificationsDTO;
import eu.michalkijowski.carvisor.services.ErrorsService;
import eu.michalkijowski.carvisor.services.NotificationService;
import eu.michalkijowski.carvisor.services.ReverseGeocodingService;

public class NotificationFragment extends Fragment {

    String regex = "";
    private ProgressDialog mProgressDialog;
    ListView listView;
    FloatingActionButton fab;
    long timestampFrom;
    long timestampTo;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
        for (int i = 0; i < menu.size(); i++)
            menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_notification_list, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(NotificationFragment.this)
                        .navigate(R.id.action_nav_notification_list_to_nav_notification,bundle);
            }
        });
        return root;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        timestampFrom = getArguments().getLong("timestampFrom");
        timestampTo = getArguments().getLong("timestampTo");
        new DownloadDataForList().execute();
    }



    private class DownloadDataForList extends AsyncTask<Void, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(Void... voids) {
            /********************************
             * ListView
             *******************************/
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
            NotificationsDTO notificationsDTO = NotificationService.getNotifications(timestampFrom, timestampTo);
            List<HashMap<String, String>> list = new ArrayList<>();
            for (NotificationDTO notificationDTO : notificationsDTO.getListOfNotification()) {
                HashMap item = new HashMap<String, String>();
                item.put("type", NotificationService.generateDescriptionOfNotification(getContext(), notificationDTO.getType(), notificationDTO.getValue()));
                item.put("driver", notificationDTO.getUserName());
                item.put("vehicle", notificationDTO.getDeviceLicensePlate());
                String location[] = notificationDTO.getLocation().split(";");
                item.put("location", ReverseGeocodingService.getReverseGeocoding(Double.parseDouble(location[0]), Double.parseDouble(location[1])).getAddress());
                item.put("date", format.format(new Date(notificationDTO.getDate()*1000)));
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_error_list_row,
                    new String[]{"type", "driver", "vehicle", "location", "date"},
                    new int[]{R.id.type, R.id.driver, R.id.vehicle, R.id.location, R.id.date});
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
            mProgressDialog.dismiss();
        }
    }
}