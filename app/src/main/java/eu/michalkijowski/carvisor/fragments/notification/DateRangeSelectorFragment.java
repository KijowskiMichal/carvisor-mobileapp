package eu.michalkijowski.carvisor.fragments.notification;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.Calendar;
import java.util.List;

import eu.michalkijowski.carvisor.R;

public class DateRangeSelectorFragment extends Fragment {
    private ProgressDialog mProgressDialog;
    private com.applandeo.materialcalendarview.CalendarView calendarView;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.home, menu);
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        super.onCreateOptionsMenu(menu,inflater);
    }

    public void showRecords(View view) {
        List<Calendar> listOfDates =  calendarView.getSelectedDates();
        if (listOfDates.size()>2) {

            //long timestampFrom = listOfDates.get(0).toInstant().atOffset(ZoneOffset.UTC).toEpochSecond();
            long offset = listOfDates.get(0).getTimeZone().getRawOffset();
            long timestampFrom = (listOfDates.get(0).getTimeInMillis()+offset)/1000;
            long timestampTo = (listOfDates.get(listOfDates.size()-1).getTimeInMillis()+offset)/1000;
            Bundle bundle = new Bundle();
            bundle.putLong("timestampFrom", timestampFrom);
            bundle.putLong("timestampTo", timestampTo);
            NavHostFragment.findNavController(DateRangeSelectorFragment.this)
                    .navigate(R.id.action_nav_notification_to_nav_notification_list,bundle);
        }
        else {
            Toast.makeText(getContext(), getString(R.string.select_date_range_before), Toast.LENGTH_LONG).show();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_date_range_selector, container, false);
        /************************
         * ustawienia akcji przycisku
         ***********************/
        Button buttonToGoOut = root.findViewById(R.id.button2);
        buttonToGoOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRecords(view);
            }
        });
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
                NavHostFragment.findNavController(DateRangeSelectorFragment.this)
                        .navigate(R.id.action_nav_notification_to_nav_notification,bundle);
            }
        });

        return root;
    }
}