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

//Dialog Fragment that shows a date picker followed by a time picker
//Implements listeners for both date and time selection
public class TimePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    // Interface for communicating the selected date and time back to the caller
    public interface DateTimeSetListener {
        void onDateTimeSet(Date date);
    }

    // Listener for the selected date and time
    private DateTimeSetListener dateTimeSetListener;

    // Calendar instance to store the selected date and time
    private Calendar selectedDateTime;

    // Factory method to create a new instance of TimePickerFragment
    public static TimePickerFragment newInstance(DateTimeSetListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.dateTimeSetListener = listener;
        fragment.selectedDateTime = Calendar.getInstance();
        return fragment;
    }

    // Creates and returns the date picker dialog
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Get current date for initial dialog values
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    // Callback for when date is set in the date picker
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Store the selected date
        selectedDateTime.set(Calendar.YEAR, year);
        selectedDateTime.set(Calendar.MONTH, month);
        selectedDateTime.set(Calendar.DAY_OF_MONTH, day);

        // Show the time picker dialog after date is selected
        TimePickerDialog timePickerDialog = new TimePickerDialog(
            requireContext(),
            this,
            selectedDateTime.get(Calendar.HOUR_OF_DAY),
            selectedDateTime.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(requireActivity())
        );
        timePickerDialog.show();
    }

    // Callback for when time is set in the time picker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Store the selected time
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDateTime.set(Calendar.MINUTE, minute);

        // Notify listener with the complete date and time
        if (dateTimeSetListener != null) {
            dateTimeSetListener.onDateTimeSet(selectedDateTime.getTime());
        }
    }
}
