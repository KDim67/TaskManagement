package hua.dit.taskmanagement.utils;

import android.content.Context;
import android.util.Log;
import java.util.Date;
import hua.dit.taskmanagement.entities.Task;

public class TaskStatusManager {
    public static final String STATUS_RECORDED = "recorded";
    public static final String STATUS_IN_PROGRESS = "in-progress";
    public static final String STATUS_EXPIRED = "expired";
    public static final String STATUS_COMPLETED = "completed";

    public static String determineTaskStatus(Task task) {
        if (task == null) return STATUS_RECORDED;

        if (STATUS_COMPLETED.equals(task.getStatus())) {
            return STATUS_COMPLETED;
        }

        Date currentTime = new Date();
        Date startTime = task.getStartTime();
        int durationHours = task.getDurationHours();

        Date endTime = new Date(startTime.getTime() + (durationHours * 60 * 60 * 1000));

        if (currentTime.before(startTime)) {
            return STATUS_RECORDED;
        }
        else if (currentTime.after(startTime) && currentTime.before(endTime)) {
            return STATUS_IN_PROGRESS;
        }
        else if (currentTime.after(endTime)) {
            return STATUS_EXPIRED;
        }

        return task.getStatus();
    }

    public static boolean canTransitionTo(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case STATUS_RECORDED:
                return newStatus.equals(STATUS_IN_PROGRESS) ||
                        newStatus.equals(STATUS_COMPLETED);

            case STATUS_IN_PROGRESS:
                return newStatus.equals(STATUS_EXPIRED) ||
                        newStatus.equals(STATUS_COMPLETED);

            case STATUS_EXPIRED:
                return newStatus.equals(STATUS_COMPLETED);

            case STATUS_COMPLETED:
                return false;

            default:
                return false;
        }
    }

    public static void logStatusTransition(Task task, String oldStatus, String newStatus) {
        Log.d("TaskStatusManager",
                String.format("Task %d status changed from %s to %s",
                        task.getUid(), oldStatus, newStatus));
    }
}
