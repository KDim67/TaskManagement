package hua.dit.taskmanagement.utils;

import android.util.Log;
import java.util.Date;
import hua.dit.taskmanagement.entities.Task;

// Utility class for managing task statuses and transitions
// Handles status determination and validation of status changes
public class TaskStatusManager {
    // Constants defining possible task statuses
    public static final String STATUS_RECORDED = "recorded";
    public static final String STATUS_IN_PROGRESS = "in-progress";
    public static final String STATUS_EXPIRED = "expired";
    public static final String STATUS_COMPLETED = "completed";

    // Determines the current status of a task based on its timing
    public static String determineTaskStatus(Task task) {
        // Return default status if task is null
        if (task == null) return STATUS_RECORDED;

        // Return completed if task is marked as completed
        if (STATUS_COMPLETED.equals(task.getStatus())) {
            return STATUS_COMPLETED;
        }

        // Calculate task timing
        Date currentTime = new Date();
        Date startTime = task.getStartTime();
        int durationHours = task.getDurationHours();

        // Calculate end time
        Date endTime = new Date(startTime.getTime() + (durationHours * 60 * 60 * 1000));

        // Determine status based on current time relative to task timeline
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

    // Validates if a status transition is allowed
    public static boolean canTransitionTo(String currentStatus, String newStatus) {
        switch (currentStatus) {
            case STATUS_RECORDED:
                // Recorded tasks can move to in-progress or completed
                return newStatus.equals(STATUS_IN_PROGRESS) ||
                        newStatus.equals(STATUS_COMPLETED);

            case STATUS_IN_PROGRESS:
                // In-progress tasks can expire or complete
                return newStatus.equals(STATUS_EXPIRED) ||
                        newStatus.equals(STATUS_COMPLETED);

            case STATUS_EXPIRED:
                // Expired tasks can only be completed
                return newStatus.equals(STATUS_COMPLETED);

            case STATUS_COMPLETED:
                // Completed tasks cannot transition
                return false;

            default:
                return false;
        }
    }

    // Logs status transitions for debugging purposes
    public static void logStatusTransition(Task task, String oldStatus, String newStatus) {
        Log.d("TaskStatusManager",
                String.format("Task %d status changed from %s to %s",
                        task.getUid(), oldStatus, newStatus));
    }
}
