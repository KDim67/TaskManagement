package hua.dit.taskmanagement.background;

import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Executor;

import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.utils.TaskStatusManager;

public class TaskStatusWork {
    private static final String TAG = "TaskStatusWork";

    private final Executor executor;
    private final Handler handler;
    private final TaskRepository taskRepository;
    private final TextView statusTextView;

    public TaskStatusWork(Executor executor, Handler handler, TaskRepository taskRepository, TextView statusTextView) {
        this.executor = executor;
        this.handler = handler;
        this.taskRepository = taskRepository;
        this.statusTextView = statusTextView;
    }

    public void checkTaskStatuses() {
        this.executor.execute(() -> {
            Log.i(TAG, "checkTaskStatuses() START - Checking task statuses");

            taskRepository.getNonCompletedTasks(new TaskRepository.DataCallback<List<Task>>() {
                @Override
                public void onDataLoaded(List<Task> tasks) {
                    for (Task task : tasks) {
                        String currentStatus = task.getStatus();
                        String newStatus = TaskStatusManager.determineTaskStatus(task);

                        if (!currentStatus.equals(newStatus) &&
                                TaskStatusManager.canTransitionTo(currentStatus, newStatus)) {

                            Log.i(TAG, "Task " + task.getUid() + " status changing from " +
                                    currentStatus + " to " + newStatus);

                            task.setStatus(newStatus);
                            updateTaskStatus(task, currentStatus, newStatus);

                            try { Thread.sleep(1000); } catch (Throwable t) {}
                        }
                    }

                    Log.i(TAG, "checkTaskStatuses() END");
                    updateAppUI("Task status check completed!");
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error checking task statuses: " + error);
                    updateAppUI("Error: " + error);
                }
            });
        });
    }

    private void updateTaskStatus(Task task, String oldStatus, String newStatus) {
        taskRepository.updateTaskStatus(task.getUid(), newStatus, new TaskRepository.OperationCallback() {
            @Override
            public void onSuccess(long result) {
                Log.i(TAG, "Successfully updated task " + task.getUid() + " status");
                TaskStatusManager.logStatusTransition(task, oldStatus, newStatus);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error updating task " + task.getUid() + " status: " + error);
            }
        });
    }

    private void updateAppUI(String message) {
        this.handler.post(() -> {
            this.statusTextView.setText(message);
        });
    }
}
