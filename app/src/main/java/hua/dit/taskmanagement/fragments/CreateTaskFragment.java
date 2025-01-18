package hua.dit.taskmanagement.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.repositories.TaskRepository.OperationCallback;

public class CreateTaskFragment extends Fragment {
    private TextInputEditText shortNameInput;
    private TextInputEditText descriptionInput;
    private TextInputEditText startTimeInput;
    private TextInputEditText durationInput;
    private TextInputEditText locationInput;
    private Button createTaskButton;
    private Calendar selectedDateTime;
    private TaskRepository taskRepository;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = new TaskRepository(requireActivity().getApplication());
        selectedDateTime = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);

        shortNameInput = view.findViewById(R.id.shortNameInput);
        descriptionInput = view.findViewById(R.id.descriptionInput);
        startTimeInput = view.findViewById(R.id.startTimeInput);
        durationInput = view.findViewById(R.id.durationInput);
        locationInput = view.findViewById(R.id.locationInput);
        createTaskButton = view.findViewById(R.id.createTaskButton);

        startTimeInput.setOnClickListener(v -> showDateTimePicker());

        createTaskButton.setOnClickListener(v -> createTask());

        return view;
    }

    private void showDateTimePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, year);
                    selectedDateTime.set(Calendar.MONTH, month);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(
                            requireContext(),
                            (view1, hourOfDay, minute) -> {
                                selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                selectedDateTime.set(Calendar.MINUTE, minute);
                                updateStartTimeDisplay();
                            },
                            selectedDateTime.get(Calendar.HOUR_OF_DAY),
                            selectedDateTime.get(Calendar.MINUTE),
                            true
                    );
                    timePickerDialog.show();
                },
                selectedDateTime.get(Calendar.YEAR),
                selectedDateTime.get(Calendar.MONTH),
                selectedDateTime.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateStartTimeDisplay() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        startTimeInput.setText(sdf.format(selectedDateTime.getTime()));
    }

    private void createTask() {
        String shortName = shortNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String durationStr = durationInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (shortName.isEmpty()) {
            shortNameInput.setError("Short name is required");
            return;
        }

        if (description.isEmpty()) {
            descriptionInput.setError("Description is required");
            return;
        }

        if (startTimeInput.getText().toString().isEmpty()) {
            startTimeInput.setError("Start time is required");
            return;
        }

        if (durationStr.isEmpty()) {
            durationInput.setError("Duration is required");
            return;
        }

        try {
            int duration = Integer.parseInt(durationStr);
            if (duration <= 0) {
                durationInput.setError("Duration must be positive");
                return;
            }

            Task task = new Task(
                    shortName,
                    description,
                    selectedDateTime.getTime(),
                    duration,
                    location
            );

            taskRepository.insertTask(task, new TaskRepository.OperationCallback() {
                @Override
                public void onSuccess(long id) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Task created successfully", Toast.LENGTH_SHORT).show();
                        clearInputs();
                    });
                }

                @Override
                public void onError(String error) {
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(requireContext(), "Error creating task: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });

        } catch (NumberFormatException e) {
            durationInput.setError("Invalid duration");
        }
    }

    private void clearInputs() {
        shortNameInput.setText("");
        descriptionInput.setText("");
        startTimeInput.setText("");
        durationInput.setText("");
        locationInput.setText("");
        selectedDateTime = Calendar.getInstance();
    }
}
