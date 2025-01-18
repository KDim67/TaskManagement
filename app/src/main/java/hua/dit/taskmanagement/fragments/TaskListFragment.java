package hua.dit.taskmanagement.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.adapters.TaskAdapter;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepositoryManager;

public class TaskListFragment extends Fragment {
    private TaskRepository taskRepository;
    private TaskAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);

        adapter = new TaskAdapter();
        recyclerView.setAdapter(adapter);

        taskRepository = TaskRepositoryManager.getInstance(getActivity().getApplication());

        loadTasks();

        adapter.setOnTaskClickListener(new TaskAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                TaskDetailsFragment detailsFragment = TaskDetailsFragment.newInstance(task.getUid());
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, detailsFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        adapter.setOnTaskLongClickListener(new TaskAdapter.OnTaskLongClickListener() {
            @Override
            public void onTaskLongClick(Task task) {
                showDeleteDialog(task);
            }
        });

        return view;
    }

    private void loadTasks() {
        if (isAdded() && getContext() != null) {
            TaskRepository repository = new TaskRepository(requireContext());
            repository.getNonCompletedTasks(new TaskRepository.DataCallback<List<Task>>() {
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

    private void showDeleteDialog(Task task) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete task: " + task.getShortName() + "?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteTask(task.getUid());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTask(int taskId) {
        if (!isAdded()) return;
        taskRepository.deleteTask(taskId, new TaskRepository.OperationCallback() {
            @Override
            public void onSuccess(long rowsAffected) {
                if (!isAdded()) return;
                showResultDialog("Task Deleted",
                        "Successfully deleted task. Rows affected: " + rowsAffected);
                loadTasks();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                showResultDialog("Error", error);
            }
        });
    }

    private void showResultDialog(String title, String message) {
        if (isAdded() && getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", null)
                    .show();
        }
    }
}