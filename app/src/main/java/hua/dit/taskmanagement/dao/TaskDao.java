package hua.dit.taskmanagement.dao;

import android.database.Cursor;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import java.util.List;

import hua.dit.taskmanagement.entities.Task;

// Data Access Object interface for Task entity
@Dao
public interface TaskDao {
    // Inserts a new task and returns its generated ID
    @Insert
    public long insert(Task task);

    // Updates an existing task and returns number of rows affected
    @Update
    public int update(Task task);

    // Retrieves all tasks from the database
    @Query("SELECT * FROM tasks")
    public List<Task> getAllTasks();

    // Retrieves all non-completed tasks
    @Query("SELECT * FROM tasks WHERE status != 'completed'")
    public List<Task> getNonCompletedTasks();

    // Retrieves a specific task by its ID
    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    public Task getTaskById(int taskId);

    // Deletes a task and returns number of rows affected
    @Delete
    public int deleteTask(Task task);

    // Deletes a task by its ID and returns number of rows affected
    @Query("DELETE FROM tasks WHERE uid = :taskId")
    public int deleteById(long taskId);

    // Updates the status of a specific task and returns number of rows affected
    @Query("UPDATE tasks SET status = :newStatus WHERE uid = :taskId")
    public int updateTaskStatus(int taskId, String newStatus);

    // Retrieves non-completed tasks ordered by status priority and start time
    // Priority order: expired -> in-progress -> recorded -> others
    @Query("SELECT * FROM tasks WHERE status != 'completed' ORDER BY " +
            "CASE status " +
            "WHEN 'expired' THEN 1 " +
            "WHEN 'in-progress' THEN 2 " +
            "WHEN 'recorded' THEN 3 " +
            "ELSE 4 END, " +
            "start_time ASC")
    List<Task> getNonCompletedTasksOrdered();

    // Returns a Cursor containing all tasks (for ContentProvider support)
    @Query("SELECT * FROM tasks")
    public Cursor getAllTasksCursor();

    // Returns a Cursor for a specific task by ID (for ContentProvider support)
    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    public Cursor getTaskByIdCursor(long taskId);
}