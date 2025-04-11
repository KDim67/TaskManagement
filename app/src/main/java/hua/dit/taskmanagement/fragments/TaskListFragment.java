package hua.dit.taskmanagement.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.File;
import java.io.IOException;
import java.util.List;
import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.adapters.TaskAdapter;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.repositories.TaskRepositoryManager;
import hua.dit.taskmanagement.utils.TaskExporter;

// Fragment for displaying a list of tasks and handling task-related operations
public class TaskListFragment extends Fragment {
    // Tag for logging purposes
    private static final String TAG = "TaskListFragment";

    // Adapter for the RecyclerView
    private TaskAdapter adapter;

    // Repository for database operations
    private TaskRepository taskRepository;

    // Initialize fragment and repository
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        taskRepository = TaskRepositoryManager.getInstance(requireActivity().getApplication()).getTaskRepository();
    }

    // Create and set up the fragment's view
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        // Set up RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        // Set up export FAB
        FloatingActionButton exportFab = view.findViewById(R.id.export_fab);
        exportFab.setOnClickListener(v -> exportTasks());

        // Initialize and set up adapter
        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        // Set up click listener for viewing task details
        adapter.setOnTaskClickListener(task -> {
            TaskDetailsFragment detailsFragment = TaskDetailsFragment.newInstance(task.getUid());
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, detailsFragment)
                    .addToBackStack(null)
                    .commit();
        });

        // Set up long click listener for delete operation
        adapter.setOnTaskLongClickListener(this::showDeleteDialog);

        // Load initial task data
        loadTasks();
        return view;
    }

    // Loads non-completed tasks from repository and updates the adapter
    private void loadTasks() {
        if (isAdded() && getContext() != null) {
            taskRepository.getNonCompletedTasksOrdered(new TaskRepository.DataCallback<List<Task>>() {
                @Override
                public void onDataLoaded(List<Task> tasks) {
                    if (isAdded()) {
                        adapter.setTasks(tasks);
                    }
                }

                @Override
                public void onError(String error) {
                    if (isAdded()) {
                        Toast.makeText(requireContext(),
                                "Error loading tasks: " + error,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    // Shows confirmation dialog before deleting a task
    private void showDeleteDialog(Task task) {
        if (isAdded() && getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete task with name: " + task.getShortName() + " and id: " + task.getUid() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteTask(task.getUid()))
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    // Deletes a task from the repository
    private void deleteTask(int taskId) {
        taskRepository.deleteTask(taskId, new TaskRepository.OperationCallback() {
            @Override
            public void onSuccess(long rowsAffected) {
                if (isAdded()) {
                    showResultDialog("Task Deleted",
                            "Successfully deleted task. Rows affected: " + rowsAffected);
                    loadTasks();
                }
            }

            @Override
            public void onError(String error) {
                if (isAdded()) {
                    showResultDialog("Error", "Failed to delete task: " + error);
                }
            }
        });
    }

    // Shows a generic result dialog with title and message
    private void showResultDialog(String title, String message) {
        if (isAdded() && getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    // Exports non-completed tasks to HTML file
    private void exportTasks() {
        Log.d(TAG, "exportTasks called");
        taskRepository.getNonCompletedTasks(new TaskRepository.DataCallback<List<Task>>() {
            @Override
            public void onDataLoaded(List<Task> tasks) {
                Log.d(TAG, "Tasks loaded, count: " + (tasks != null ? tasks.size() : 0));
                try {
                    // Export tasks to HTML file
                    File htmlFile = TaskExporter.exportTasksToHtml(requireContext(), tasks);

                    // Show success message
                    String message = String.format("Tasks exported to Downloads:\n%s",
                            htmlFile.getName());
                    Log.d(TAG, "Export successful: " + message);
                    Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // Handle export failure
                    Log.e(TAG, "Export failed", e);
                    Toast.makeText(requireContext(),
                            "Failed to export tasks: " + e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load tasks: " + error);
                Toast.makeText(requireContext(),
                        "Failed to load tasks: " + error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}