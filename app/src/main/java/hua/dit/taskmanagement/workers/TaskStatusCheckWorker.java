package hua.dit.taskmanagement.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.repositories.TaskRepositoryManager;
import hua.dit.taskmanagement.utils.TaskStatusManager;

//A background worker class that periodically checks and updates the status of tasks.
//This worker extends AndroidX Worker to handle background processing of task status updates.
public class TaskStatusCheckWorker extends Worker {
    private static final String TAG = "TaskStatusCheckWorker";
    private final TaskRepository taskRepository;

    // Constructor initializes the worker with context and parameters.
    // Validates that the context is an Application context and initializes the task repository.
    public TaskStatusCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Context appContext = context.getApplicationContext();
        if (!(appContext instanceof android.app.Application)) {
            throw new IllegalStateException("Context must be an Application context");
        }
        this.taskRepository = TaskRepositoryManager.getInstance((android.app.Application) appContext).getTaskRepository();
    }

    // Executes the main work of checking and updating task statuses.
    // Retrieves non-completed tasks and processes their status transitions.
    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork() START - Checking task statuses");

        // Retrieve all non-completed tasks and process them
        taskRepository.getNonCompletedTasks(new TaskRepository.DataCallback<List<Task>>() {
            @Override
            public void onDataLoaded(List<Task> tasks) {
                // Iterate through each task to check and update its status
                for (Task task : tasks) {
                    Log.i(TAG, "Checking task: " + task.getUid());

                    String currentStatus = task.getStatus();
                    String newStatus = TaskStatusManager.determineTaskStatus(task);

                    // Check if status needs to be updated and if the transition is valid
                    if (!currentStatus.equals(newStatus) &&
                            TaskStatusManager.canTransitionTo(currentStatus, newStatus)) {

                        Log.i(TAG, "Updating task " + task.getUid() + " status from " +
                                currentStatus + " to " + newStatus);

                        task.setStatus(newStatus);
                        updateTaskStatus(task, currentStatus, newStatus);

                        // Add delay between updates to prevent overwhelming the system
                        try { Thread.sleep(1000); } catch (Throwable t) {}
                    }
                }
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Error checking task statuses: " + error);
            }
        });

        Log.i(TAG, "doWork() END");
        return Result.success();
    }

    // Updates the task status in the repository and logs the transition.
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
}
