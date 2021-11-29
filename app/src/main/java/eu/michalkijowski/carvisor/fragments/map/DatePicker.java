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
    String timestamp;
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
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        timestamp = String.valueOf(calendar.getTimeInMillis()/1000);
        System.out.println(timestamp);
        SimpleDateFormat changeFormat = new SimpleDateFormat("dd.MM.yyyy");
        date = changeFormat.format(calendar.getTime());
        MapFragment.timestamp = timestamp;
        MapFragment.date = date;
        MapFragment.flag = true;
    }

    public String getDate() {
        return date;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
