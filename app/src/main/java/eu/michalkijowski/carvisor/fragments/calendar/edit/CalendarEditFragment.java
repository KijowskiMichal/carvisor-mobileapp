package eu.michalkijowski.carvisor.fragments.calendar.edit;

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
import eu.michalkijowski.carvisor.data_models.EventEditDTO;
import eu.michalkijowski.carvisor.services.CalendarService;
import eu.michalkijowski.carvisor.services.MapService;

public class CalendarEditFragment extends Fragment {
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

    public EventEditDTO register(View view) {
        try {
            Bundle bundle = getArguments();

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            String title = this.title.getText().toString();
            String description = this.description.getText().toString();
            Date dateFrom = dateFormat.parse(this.dateFrom.getText().toString());
            Date dateTo = dateFormat.parse(this.dateTo.getText().toString());
            String type = EventTypeConverter.getTypeFromPosition(this.type.getSelectedItemPosition());
            int device = deviceNamesDTOS[this.device.getSelectedItemPosition()].getId();
            boolean remind = this.remind.isChecked();

            if (title.equals("")) return null;
            if (description.equals("")) return null;
            if (dateFrom.equals("")) return null;
            if (dateTo.equals("")) return null;
            if (type.equals("")) return null;

            EventEditDTO eventEditDTO = new EventEditDTO();
            eventEditDTO.setTitle(title);
            eventEditDTO.setDescription(description);
            eventEditDTO.setStart(atStartOfDay(dateFrom).getTime()/1000);
            eventEditDTO.setEnd(atEndOfDay(dateTo).getTime()/1000);
            eventEditDTO.setType(type);
            eventEditDTO.setDevice(device);
            eventEditDTO.setRemind(remind);
            eventEditDTO.setDraggable(true);
            eventEditDTO.setId(bundle.getInt("id"));

            return eventEditDTO;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_calendar_edit, container, false);
        Button registerButton = root.findViewById(R.id.button2);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventEditDTO eventEditDTO = register(view);
                if (eventEditDTO!=null) {
                    new UpdateEvent().execute(eventEditDTO);
                }
                else {
                    Toast.makeText(getContext(), "Nie wszystkie pola wypełniono poprawnie.", Toast.LENGTH_LONG).show();
                }
            }
        });
        requireActivity().getOnBackPressedDispatcher().addCallback(getActivity(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(CalendarEditFragment.this)
                        .navigate(R.id.action_nav_calendar_edit_to_nav_calendar,bundle);
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
        new GetDevices().execute();
        return root;
    }

    private class UpdateEvent extends AsyncTask<EventEditDTO, Void, Boolean> {
        @Override
        protected Boolean doInBackground(EventEditDTO... eventEditDTOS) {
            Bundle bundle = getArguments();
            return CalendarService.editEvent(eventEditDTOS[0], bundle.getInt("id"));
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
                Toast.makeText(getContext(), "Poprawnie zedytowano zdarzenie.", Toast.LENGTH_LONG).show();
                Bundle bundle = new Bundle();
                NavHostFragment.findNavController(CalendarEditFragment.this)
                        .navigate(R.id.action_nav_calendar_edit_to_nav_calendar,bundle);
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