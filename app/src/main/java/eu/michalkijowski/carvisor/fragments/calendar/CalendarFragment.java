package eu.michalkijowski.carvisor.fragments.calendar;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.applandeo.materialcalendarview.CalendarUtils;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.converters.GlobalConfigurationConverter;
import eu.michalkijowski.carvisor.data_models.GlobalSettingsDTO;
import eu.michalkijowski.carvisor.data_models.SetGlobalSettingsDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.services.SettingsService;
import eu.michalkijowski.carvisor.services.UsersService;

public class CalendarFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private com.applandeo.materialcalendarview.CalendarView calendarView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public SetGlobalSettingsDTO setSettings(View view) {
        try {
            String history = ((Spinner)getView().findViewById(R.id.spinner)).getSelectedItem().toString();
            String send = ((Spinner)getView().findViewById(R.id.spinner2)).getSelectedItem().toString();
            String location = ((Spinner)getView().findViewById(R.id.spinner3)).getSelectedItem().toString();

            if (history.equals("")) return null;
            if (send.equals("")) return null;
            if (location.equals("")) return null;

            SetGlobalSettingsDTO setGlobalSettingsDTO = new SetGlobalSettingsDTO();
            setGlobalSettingsDTO.setGetLocationInterval(String.valueOf(GlobalConfigurationConverter.getIntFromLocationInterval(location)));
            setGlobalSettingsDTO.setHistoryTimeout(String.valueOf(GlobalConfigurationConverter.getIntFromHistoryInterval(history)));
            setGlobalSettingsDTO.setSendInterval(String.valueOf(GlobalConfigurationConverter.getIntFromSendInterval(send)));

            return setGlobalSettingsDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        /************************
         * ustawienia kalendarza
         ***********************/
        calendarView = root.findViewById(R.id.calendarView);
        calendarView.setSelected(true);
        try {
            calendarView.setDate(Calendar.getInstance());
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
            }
        });
        /************************
         * reszta widoku
         ***********************/
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(CalendarFragment.this)
                        .navigate(R.id.action_nav_calendar_to_nav_calendar,bundle);
            }
        });

        new GetData().execute();

        return root;
    }

    private class GetData extends AsyncTask<Void, Void, List<EventDay>> {
        @Override
        protected List<EventDay> doInBackground(Void... params) {
            List<EventDay> events = new ArrayList<>();
            Calendar calendar = Calendar.getInstance();
            events.add(new EventDay(calendar, R.drawable.circle));
            events.add(new EventDay(calendar, R.drawable.circle));
            return events;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(List<EventDay> eventsList) {
            super.onPostExecute(eventsList);
            calendarView.setEvents(eventsList);
            mProgressDialog.dismiss();
        }
    }

    private class EditUserPassword extends AsyncTask<UserPasswordDTO, Void, Void> {
        @Override
        protected Void doInBackground(UserPasswordDTO... userDataDTO) {
            UsersService.editUserPassword(userDataDTO[0]);
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Void param) {
            super.onPostExecute(param);
            mProgressDialog.dismiss();
            ((EditText)getView().findViewById(R.id.editTextTextPersonName5)).setText("");
            ((EditText)getView().findViewById(R.id.editTextTextPersonName6)).setText("");
            Toast.makeText(getContext(), "Poprawnie zedytowano hasło.", Toast.LENGTH_LONG).show();
        }
    }
}