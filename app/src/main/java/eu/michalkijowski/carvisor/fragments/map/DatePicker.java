package eu.michalkijowski.carvisor.fragments.map;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    String startTimestamp;
    String endTimestamp;
    String date;
    MapFragment fragment;

    int day;
    int month;
    int year;

    public DatePicker(int day, int month, int year) {
        this.day = day;
        this.month = month;
        this.year = year;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void show(@NonNull FragmentManager manager, @Nullable String tag, MapFragment mapFragment) {
        fragment = mapFragment;
        super.show(manager, tag);
    }

    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int day) {
        Calendar end = Calendar.getInstance();
        end.set(Calendar.YEAR, year);
        end.set(Calendar.MONTH, month);
        end.set(Calendar.DAY_OF_MONTH, day);
        end.set(Calendar.HOUR_OF_DAY, 23);
        end.set(Calendar.MINUTE, 59);
        end.set(Calendar.SECOND, 59);
        end.set(Calendar.MILLISECOND, 999);
        Calendar start = Calendar.getInstance();
        start.set(Calendar.YEAR, year);
        start.set(Calendar.MONTH, month);
        start.set(Calendar.DAY_OF_MONTH, day);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        startTimestamp = String.valueOf(start.getTimeInMillis()/1000);
        endTimestamp = String.valueOf(end.getTimeInMillis()/1000);
        SimpleDateFormat changeFormat = new SimpleDateFormat("dd.MM.yyyy");
        date = changeFormat.format(start.getTime());
        MapFragment.startTimestamp = startTimestamp;
        MapFragment.endTimestamp = endTimestamp;
        MapFragment.date = date;
        MapFragment.flag = true;
    }

    public String getStartTimestamp() {
        return startTimestamp;
    }

    public String getEndTimestamp() {
        return endTimestamp;
    }

    public String getDate() {
        return date;
    }
}
