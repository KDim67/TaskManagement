package hua.dit.taskmanagement.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

import hua.dit.taskmanagement.entities.Task;

@Dao
public interface TaskDao {
    @Insert
    public long insertTask(Task task);

    @Query("SELECT * FROM tasks")
    public List<Task> getAllTasks();

    @Query("SELECT * FROM tasks WHERE status != 'completed'")
    public List<Task> getNonCompletedTasks();

    @Query("SELECT * FROM tasks WHERE uid = :taskId")
    public Task getTaskById(int taskId);

    @Delete
    public int deleteTask(Task task);

    @Query("UPDATE tasks SET status = :newStatus WHERE uid = :taskId")
    public int updateTaskStatus(int taskId, String newStatus);
}