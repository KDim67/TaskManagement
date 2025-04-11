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

import java.util.Date;
import java.util.Locale;
import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.repositories.TaskRepositoryManager;

// Fragment for creating new tasks with user input
public class CreateTaskFragment extends Fragment implements TimePickerFragment.DateTimeSetListener {
    // UI elements for task creation form
    private EditText shortNameInput;
    private EditText descriptionInput;
    private EditText durationInput;
    private EditText locationInput;
    private TextView startTimeText;
    private Button selectTimeButton;
    private Button createButton;

    // Repository for database operations
    private TaskRepository taskRepository;

    // Selected date/time for the task
    private Date selectedDateTime;

    // Date formatter for displaying time
    private SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    // Initialize fragment and repository
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = TaskRepositoryManager.getInstance(requireActivity().getApplication()).getTaskRepository();
        selectedDateTime = new Date();
    }

    // Create and set up the fragment's view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_task, container, false);

        // Initialize UI elements
        shortNameInput = view.findViewById(R.id.input_short_name);
        descriptionInput = view.findViewById(R.id.input_description);
        durationInput = view.findViewById(R.id.input_duration);
        locationInput = view.findViewById(R.id.input_location);
        startTimeText = view.findViewById(R.id.text_start_time);
        selectTimeButton = view.findViewById(R.id.button_select_time);
        createButton = view.findViewById(R.id.button_create);

        // Set initial time display
        updateTimeDisplay();

        // Set up button click listeners
        selectTimeButton.setOnClickListener(v -> showTimePickerDialog());
        createButton.setOnClickListener(v -> createTask());

        return view;
    }

    // Shows the time picker dialog for selecting task start time
    private void showTimePickerDialog() {
        TimePickerFragment timePickerFragment = TimePickerFragment.newInstance(this);
        timePickerFragment.show(getChildFragmentManager(), "timePicker");
    }

    // Callback method when date/time is set in picker
    @Override
    public void onDateTimeSet(Date date) {
        selectedDateTime = date;
        updateTimeDisplay();
    }

    // Updates the displayed time in the UI
    private void updateTimeDisplay() {
        startTimeText.setText("Start Time: " + timeFormat.format(selectedDateTime));
    }

    // Validates input and creates a new task
    private void createTask() {
        // Get input values
        String shortName = shortNameInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String durationStr = durationInput.getText().toString().trim();
        String location = locationInput.getText().toString().trim();

        // Validate required fields
        if (shortName.isEmpty() || description.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(requireContext(),
                    "Please fill in all required fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate and parse duration
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

        // Create and insert new task
        Task newTask = new Task(shortName, description, selectedDateTime, durationHours, location);
        newTask.setStatus("recorded");

        taskRepository.insertTask(newTask, new TaskRepository.OperationCallback() {

            // Handle successful task creation
            @Override
            public void onSuccess(long result) {
                if (isAdded()) {
                    Toast.makeText(requireContext(),
                            "Task created successfully",
                            Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            // Handle task creation error
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
