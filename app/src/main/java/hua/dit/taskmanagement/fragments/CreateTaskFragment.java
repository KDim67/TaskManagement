package hua.dit.taskmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;

public class CreateTaskFragment extends Fragment implements TimePickerFragment.DateTimeSetListener {
    private EditText shortNameInput;
    private EditText descriptionInput;
    private EditText durationInput;
    private EditText locationInput;
    private TextView startTimeText;
    private Button selectTimeButton;
    private Button createButton;
    private TaskRepository taskRepository;
    private Date selectedDateTime;
    private SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = new TaskRepository(requireContext());
        selectedDateTime = new Date();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);

        shortNameInput = view.findViewById(R.id.input_short_name);
        descriptionInput = view.findViewById(R.id.input_description);
        durationInput = view.findViewById(R.id.input_duration);
        locationInput = view.findViewById(R.id.input_location);
        startTimeText = view.findViewById(R.id.text_start_time);
        selectTimeButton = view.findViewById(R.id.button_select_time);
        createButton = view.findViewById(R.id.button_create);

        updateTimeDisplay();

        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());
        createButton.setOnClickListener(v -> createTask());

        return view;
    }

    private void showTimePickerDialog() {
        TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(this);
        timePickerFragment.show(getChildFragmentManager(), "timePicker");
    }

    @Override
    public void onDateTimeSet(Date date) {
        selectedDateTime = date;
        updateTimeDisplay();
    }

    private void updateTimeDisplay() {
        startTimeText.setText("Start Time: " + timeFormat.format(selectedDateTime));
    }

    private void createTask() {
        String shortName = shortNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String durationStr = durationInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        if (shortName.isEmpty() || description.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(requireContext(),
                "Please fill in all required fields",
                Toast.LENGTH_SHORT).show();
            return;
        }

        Integer durationHours;
        try {
            durationHours = Integer.parseInt(durationStr);
            if (durationHours <= 0) {
                Toast.makeText(requireContext(),
                    "Duration must be greater than 0",
                    Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(requireContext(),
                "Please enter a valid duration in hours",
                Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(shortName, description, selectedDateTime, durationHours, location);
        newTask.setStatus("recorded");

        taskRepository.insertTask(newTask, new TaskRepository.OperationCallback() {
            @Override
            public void onSuccess(long result) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                        "Task created successfully",
                        Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                        "Error creating task: " + error,
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
