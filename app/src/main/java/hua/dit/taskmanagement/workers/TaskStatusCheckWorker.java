package hua.dit.taskmanagement.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;

import hua.dit.taskmanagement.entities.Task;
import hua.dit.taskmanagement.repositories.TaskRepository;
import hua.dit.taskmanagement.utils.TaskStatusManager;

public class TaskStatusCheckWorker extends Worker {
    private static final String TAG = "TaskStatusCheckWorker";
    private final TaskRepository taskRepository;

    public TaskStatusCheckWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.taskRepository = new TaskRepository(context.getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.i(TAG, "doWork() START - Checking task statuses");

        taskRepository.getNonCompletedTasks(new TaskRepository.DataCallback<List<Task>>() {
            @Override
            public void onDataLoaded(List<Task> tasks) {
                for (Task task : tasks) {
                    Log.i(TAG, "Checking task: " + task.getUid());

                    String currentStatus = task.getStatus();
                    String newStatus = TaskStatusManager.determineTaskStatus(task);

                    if (!currentStatus.equals(newStatus) &&
                            TaskStatusManager.canTransitionTo(currentStatus, newStatus)) {

                        Log.i(TAG, "Updating task " + task.getUid() + " status from " +
                                currentStatus + " to " + newStatus);

                        task.setStatus(newStatus);
                        updateTaskStatus(task, currentStatus, newStatus);

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
