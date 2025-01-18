package hua.dit.taskmanagement.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Locale;
import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;

public class TaskDetailsFragment extends Fragment {
    private static final String ARG_TASK_ID = "task_id";
    private TaskRepository taskRepository;
    private Task currentTask;
    private Button viewMapButton;
    private Button completeButton;
    private View rootView;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    public static TaskDetailsFragment newInstance(int taskId) {
        TaskDetailsFragment fragment = new TaskDetailsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TASK_ID, taskId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = new TaskRepository(requireContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_task_details, container, false);

        TextView taskIdView = rootView.findViewById(R.id.task_id);
        TextView shortNameView = rootView.findViewById(R.id.short_name);
        TextView descriptionView = rootView.findViewById(R.id.description);
        TextView startTimeView = rootView.findViewById(R.id.start_time);
        TextView durationView = rootView.findViewById(R.id.duration);
        TextView locationView = rootView.findViewById(R.id.location);
        TextView statusView = rootView.findViewById(R.id.status);
        completeButton = rootView.findViewById(R.id.complete_button);
        viewMapButton = rootView.findViewById(R.id.view_map_button);

        int taskId = getArguments().getInt(ARG_TASK_ID, -1);
        if (taskId != -1) {
            loadTask(taskId, taskIdView, shortNameView, descriptionView, 
                    startTimeView, durationView, locationView, statusView);
        }

        completeButton.setOnClickListener(v -> markTaskAsCompleted(taskId));
        viewMapButton.setOnClickListener(v -> openLocationInMaps());

        return rootView;
    }

    private void loadTask(int taskId, TextView taskIdView, TextView shortNameView,
                         TextView descriptionView, TextView startTimeView,
                         TextView durationView, TextView locationView, TextView statusView) {
        taskRepository.getTaskById(taskId, new TaskRepository.DataCallback<Task>() {
            @Override
            public void onDataLoaded(Task task) {
                if (isAdded() && task != null) {
                    currentTask = task;
                    taskIdView.setText("Task ID: " + task.getUid());
                    shortNameView.setText(task.getShortName());
                    descriptionView.setText(task.getDescription());
                    startTimeView.setText("Start Time: " + dateFormat.format(task.getStartTime()));
                    durationView.setText("Duration: " + task.getDurationHours() + " hour(s)");
                    
                    String location = task.getLocation();
                    if (!TextUtils.isEmpty(location)) {
                        locationView.setText("Location: " + location);
                        viewMapButton.setVisibility(View.VISIBLE);
                    } else {
                        locationView.setText("Location: Not specified");
                        viewMapButton.setVisibility(View.GONE);
                    }
                    
                    statusView.setText("Status: " + task.getStatus());
                    
                    // Hide complete button if task is already completed
                    if ("completed".equals(task.getStatus())) {
                        completeButton.setVisibility(View.GONE);
                    } else {
                        completeButton.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), 
                        "Error loading task: " + error, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void markTaskAsCompleted(int taskId) {
        taskRepository.updateTaskStatus(taskId, "completed", new TaskRepository.OperationCallback() {
            @Override
            public void onSuccess(long rowsAffected) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), 
                        "Task marked as completed", 
                        Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), 
                        "Error updating task: " + error, 
                        Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openLocationInMaps() {
        if (currentTask != null && !TextUtils.isEmpty(currentTask.getLocation())) {
            try {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(currentTask.getLocation()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                startActivity(mapIntent);
            } catch (Exception e) {
                Toast.makeText(requireContext(), 
                    "Could not open maps application", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
}