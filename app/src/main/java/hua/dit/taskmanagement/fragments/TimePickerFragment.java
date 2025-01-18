package hua.dit.taskmanagement.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    public interface DateTimeSetListener {
        void onDateTimeSet(Date date);
    }

    private DateTimeSetListener dateTimeSetListener;
    private Calendar selectedDateTime;

    public static TimePickerFragment newInstance(DateTimeSetListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.dateTimeSetListener = listener;
        fragment.selectedDateTime = Calendar.getInstance();
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        selectedDateTime.set(Calendar.YEAR, year);
        selectedDateTime.set(Calendar.MONTH, month);
        selectedDateTime.set(Calendar.DAY_OF_MONTH, day);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            this,
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(requireActivity())
        );
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDateTime.set(Calendar.MINUTE, minute);
        
        if (dateTimeSetListener != null) {
            dateTimeSetListener.onDateTimeSet(selectedDateTime.getTime());
        }
    }
}
