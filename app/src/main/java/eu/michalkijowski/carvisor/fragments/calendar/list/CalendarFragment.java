package eu.michalkijowski.carvisor.fragments.calendar.list;

import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.data_models.EventDTO;
import eu.michalkijowski.carvisor.data_models.UserPasswordDTO;
import eu.michalkijowski.carvisor.services.CalendarService;
import eu.michalkijowski.carvisor.services.UsersService;

public class CalendarFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private com.applandeo.materialcalendarview.CalendarView calendarView;
    private ListView listView;
    private int day;
    private int month;
    private int year;
    private int downloadedMonth = 0;
    private int downloadedYear = 0;
    EventDTO[] eventDTOS;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_calendar, container, false);
        listView = (ListView) root.findViewById(R.id.myFleetListView);
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
        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                month=month+1;
                if (month==12) month = 0;
                new GetData().execute();
            }
        });
        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                month=month-1;
                if (month==-1) month = 11;
                new GetData().execute();
            }
        });
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                day = clickedDayCalendar.get(Calendar.DAY_OF_MONTH);
                month = clickedDayCalendar.get(Calendar.MONTH);
                year = clickedDayCalendar.get(Calendar.YEAR);
                new UpdateListOfEvents().execute();
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
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        new GetData().execute();

        return root;
    }

    private class UpdateListOfEvents extends AsyncTask<Void, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(Void... voids) {
            /********************************
             * ListView
             *******************************/
            if ((downloadedMonth!=month) || (downloadedYear!=year)) {
                eventDTOS = CalendarService.getEvents(month+1, year);
                downloadedMonth = month;
                downloadedYear = year;
            }

            EventDTO[] events = Arrays.stream(eventDTOS).filter(eventDTO -> {
                Calendar startDay = Calendar.getInstance();
                startDay.setTimeInMillis(eventDTO.getStart()*1000);
                Calendar endDay = Calendar.getInstance();
                endDay.setTimeInMillis(eventDTO.getEnd()*1000);
                return startDay.get(Calendar.DAY_OF_MONTH)<=day && endDay.get(Calendar.DAY_OF_MONTH)>=day;
            }).toArray(EventDTO[]::new);
            List<HashMap<String, String>> list = new ArrayList<>();
            for (EventDTO eventDTO : events) {
                HashMap item = new HashMap<String, String>();
                item.put("userId", String.valueOf(eventDTO.getId()));
                item.put("name", eventDTO.getTitle());
                item.put("description", eventDTO.getDescription());
                list.add(item);
            }
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), list,
                    R.layout.fragment_calendar_row,
                    new String[]{"userId", "name", "description"},
                    new int[]{R.id.userId, R.id.name, R.id.description});
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
                    rowActionDialog.show(getFragmentManager(), String.valueOf(identifier), name, CalendarFragment.this, bundle, getContext());
                }

            });
            mProgressDialog.dismiss();
        }
    }

    private class GetData extends AsyncTask<Void, Void, List<EventDay>> {
        @Override
        protected List<EventDay> doInBackground(Void... params) {
            if ((downloadedMonth!=month) || (downloadedYear!=year)) {
                eventDTOS = CalendarService.getEvents(month+1, year);
                downloadedMonth = month;
                downloadedYear = year;
            }
            List<EventDay> events = new ArrayList<>();
            for (EventDTO eventDTO : eventDTOS) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(eventDTO.getStart()*1000);
                events.add(new EventDay(calendar, R.drawable.circle));
            }
            return events;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        protected void onPostExecute(List<EventDay> eventsList) {
            super.onPostExecute(eventsList);
            calendarView.setEvents(eventsList);
            SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), List.of(),
                    R.layout.fragment_calendar_row,
                    new String[]{"userId", "name", "description"},
                    new int[]{R.id.userId, R.id.name, R.id.description});
            listView.setAdapter(simpleAdapter);
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