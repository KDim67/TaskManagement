package hua.dit.taskmanagement.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepositoryManager;

public class TaskDetailsFragment extends Fragment {
    private static final String ARG_TASK_ID = "task_id";
    private TaskRepository taskRepository;
    private int taskId;

    private TextView textViewTitle;
    private TextView textViewDescription;
    private TextView textViewStartTime;
    private TextView textViewDuration;
    private TextView textViewLocation;
    private TextView textViewStatus;
    private Button buttonComplete;
    private Button buttonViewMap;

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
        if (getArguments() != null) {
            taskId = getArguments().getInt(ARG_TASK_ID);
        }
        taskRepository = TaskRepositoryManager.getInstance(getActivity().getApplication());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_details, container, false);

        textViewTitle = view.findViewById(R.id.text_view_detail_title);
        textViewDescription = view.findViewById(R.id.text_view_detail_description);
        textViewStartTime = view.findViewById(R.id.text_view_detail_start_time);
        textViewDuration = view.findViewById(R.id.text_view_detail_duration);
        textViewLocation = view.findViewById(R.id.text_view_detail_location);
        textViewStatus = view.findViewById(R.id.text_view_detail_status);
        buttonComplete = view.findViewById(R.id.button_complete);
        buttonViewMap = view.findViewById(R.id.button_view_map);

        loadTaskDetails();

        buttonComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeTask();
            }
        });

        buttonViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });

        return view;
    }

    private void loadTaskDetails() {
        taskRepository.getTaskById(taskId, new TaskRepository.DataCallback<Task>() {
            @Override
            public void onDataLoaded(Task task) {
                if (getActivity() == null || !isAdded()) return;

                textViewTitle.setText(task.getShortName());
                textViewDescription.setText(task.getDescription());
                textViewStartTime.setText(task.getStartTime().toString());
                textViewDuration.setText(task.getDurationHours() + " hours");
                textViewLocation.setText(task.getLocation());
                textViewStatus.setText(task.getStatus());

                buttonViewMap.setVisibility(
                        task.getLocation() != null && !task.getLocation().isEmpty()
                                ? View.VISIBLE : View.GONE
                );

                buttonComplete.setVisibility(
                        "completed".equals(task.getStatus())
                                ? View.GONE : View.VISIBLE
                );
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null || !isAdded()) return;
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void completeTask() {
        taskRepository.updateTaskStatus(taskId, "completed",
                new TaskRepository.OperationCallback() {
                    @Override
                    public void onSuccess(long result) {
                        if (getActivity() == null || !isAdded()) return;
                        Toast.makeText(getActivity(), "Task completed!",
                                Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    @Override
                    public void onError(String error) {
                        if (getActivity() == null || !isAdded()) return;
                        Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openMap() {
        taskRepository.getTaskById(taskId, new TaskRepository.DataCallback<Task>() {
            @Override
            public void onDataLoaded(Task task) {
                if (getActivity() == null || !isAdded()) return;

                String location = task.getLocation();
                if (location != null && !location.isEmpty()) {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location));
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");

                    if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                        startActivity(mapIntent);
                    } else {
                        Toast.makeText(getActivity(), "Maps app not found",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() == null || !isAdded()) return;
                Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}