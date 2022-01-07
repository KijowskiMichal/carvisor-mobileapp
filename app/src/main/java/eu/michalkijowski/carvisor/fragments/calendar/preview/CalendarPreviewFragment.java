package eu.michalkijowski.carvisor.fragments.calendar.preview;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import eu.michalkijowski.carvisor.R;
import eu.michalkijowski.carvisor.converters.EventTypeConverter;
import eu.michalkijowski.carvisor.data_models.DeviceNamesDTO;
import eu.michalkijowski.carvisor.data_models.EventAddDTO;
import eu.michalkijowski.carvisor.data_models.EventDTO;
import eu.michalkijowski.carvisor.services.CalendarService;
import eu.michalkijowski.carvisor.services.MapService;

public class CalendarPreviewFragment extends Fragment {
    ImageView imageView;
    String image;
    private ProgressDialog mProgressDialog;
    ArrayAdapter<CharSequence> typeOfEventAdapter;
    ArrayAdapter<CharSequence> deviceAdapter;
    DeviceNamesDTO[] deviceNamesDTOS;

    EditText title;
    EditText description;
    EditText dateFrom;
    EditText dateTo;
    Spinner type;
    Spinner device;
    CheckBox remind;

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
        View root = inflater.inflate(R.layout.fragment_calendar_preview, container, false);
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(CalendarPreviewFragment.this)
                        .navigate(R.id.action_nav_calendar_preview_to_nav_calendar,bundle);
            }
        });
        /********************************
         * Initialize fields
         *******************************/
        title = (EditText)root.findViewById(R.id.editTextTextPersonName5);
        description = (EditText)root.findViewById(R.id.editTextTextPersonName6);
        dateFrom = (EditText)root.findViewById(R.id.editTextDate3);
        dateTo = (EditText)root.findViewById(R.id.editTextDate4);
        type = (Spinner)root.findViewById(R.id.spinner);
        device = (Spinner)root.findViewById(R.id.spinner2);
        remind = (CheckBox)root.findViewById(R.id.checkBox);
        /********************************
         * Dates
         *******************************/
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        dateFrom.setText(dateFormat.format(new Date()));
        dateTo.setText(dateFormat.format(new Date()));
        /********************************
         * Select (Spinners)
         *******************************/
        typeOfEventAdapter = ArrayAdapter.createFromResource(root.getContext(), R.array.typeOfEvent, android.R.layout.simple_spinner_item);
        typeOfEventAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(typeOfEventAdapter);
        type.setEnabled(false);
        new GetDevices().execute();
        return root;
    }

    private class PostNewEvent extends AsyncTask<EventAddDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(EventAddDTO... eventAddDTOS) {
            return CalendarService.addEvent(eventAddDTOS[0]);
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
                Toast.makeText(getContext(), "Poprawnie dodano zdarzenie.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(CalendarPreviewFragment.this)
                        .navigate(R.id.action_nav_calendar_add_to_nav_calendar,bundle);
            }
            else {
                Toast.makeText(getContext(), "Coś poszło nie tak.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GetDevices extends AsyncTask<Void, Void, Pair<ArrayAdapter, EventDTO>> {
        @Override
        protected Pair<ArrayAdapter, EventDTO> doInBackground(Void... voids) {
            /********************************
             * Device list refill
             *******************************/
            deviceNamesDTOS = MapService.getDeviceList("");
            ArrayAdapter deviceAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, (String[]) Arrays.stream(deviceNamesDTOS).map(x -> x.getName()).toArray(String[]::new));
            deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            /********************************
             * refill fields from API
             *******************************/
            Bundle bundle = getArguments();
            EventDTO eventDTO = CalendarService.getEvent(bundle.getInt("id"));
            return new Pair<>(deviceAdapter, eventDTO);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(getActivity(), "", getString(R.string.loading), true, false);
        }

        @Override
        protected void onPostExecute(Pair<ArrayAdapter, EventDTO> pair) {
            super.onPostExecute(pair);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            device.setAdapter(pair.first);
            device.setEnabled(false);
            title.setText(pair.second.getTitle());
            description.setText(pair.second.getDescription());
            dateFrom.setText(dateFormat.format(new Date(pair.second.getStart()*1000)));
            dateTo.setText(dateFormat.format(new Date(pair.second.getEnd()*1000)));
            type.setSelection(EventTypeConverter.getPositionFromType(pair.second.getType()));
            int postion = 0;
            for (DeviceNamesDTO deviceNamesDTO : deviceNamesDTOS) {
                if (deviceNamesDTO.getId() == pair.second.getDevice()) {
                    device.setSelection(postion);
                }
                postion++;
            }
            remind.setChecked(pair.second.isRemind());
            mProgressDialog.dismiss();
        }
    }

    public Date atEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    public Date atStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}