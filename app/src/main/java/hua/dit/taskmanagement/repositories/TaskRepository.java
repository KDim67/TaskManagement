package hua.dit.taskmanagement.repositories;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.room.Room;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.os.Handler;
import android.os.Looper;

import hua.dit.taskmanagement.database.TaskDatabase;
import hua.dit.taskmanagement.dao.TaskDao;
import hua.dit.taskmanagement.entities.Task;

public class TaskRepository {
    private TaskDao taskDao;
    private ExecutorService executorService;
    private final TaskDatabase database;

    public TaskRepository(Context context) {
        Context appContext = context.getApplicationContext();
        database = Room.databaseBuilder(
                appContext,
                TaskDatabase.class,
                "task_database"
        ).build();

        taskDao = database.taskDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public void insertTask(Task task, final OperationCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    long id = taskDao.insertTask(task);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onSuccess(id);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void getNonCompletedTasks(final DataCallback<List<Task>> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    List<Task> tasks = taskDao.getNonCompletedTasks();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onDataLoaded(tasks);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void getNonCompletedTasksOrdered(final DataCallback<List<Task>> callback) {
        executorService.execute(() -> {
            try {
                List<Task> tasks = taskDao.getNonCompletedTasksOrdered();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) {
                        callback.onDataLoaded(tasks);
                    }
                });
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (callback != null) {
                        callback.onError(e.getMessage());
                    }
                });
            }
        });
    }

    public void updateTaskStatus(final int taskId, final String newStatus, final OperationCallback callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    int rowsAffected = taskDao.updateTaskStatus(taskId, newStatus);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if(callback != null) {
                            callback.onSuccess(rowsAffected);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void getTaskById(final int taskId, final DataCallback<Task> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Task task = taskDao.getTaskById(taskId);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if(callback != null) {
                            callback.onDataLoaded(task);
                        }
                    });
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (callback != null) {
                            callback.onError(e.getMessage());
                        }
                    });
                }
            }
        });
    }

    public void deleteTask(int taskId, final OperationCallback callback) {
        executorService.execute(() -> {
            try {
                Task task = taskDao.getTaskById(taskId);
                if (task != null) {
                    int rowsAffected = taskDao.deleteTask(task);
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onSuccess(rowsAffected);
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        callback.onError("Task not found");
                    });
                }
            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    callback.onError("Error deleting task: " + e.getMessage());
                });
            }
        });
    }

    public interface OperationCallback {
        void onSuccess(long result);
        void onError(String error);
    }

    public interface DataCallback<T> {
        void onDataLoaded(T data);
        void onError(String error);
    }
}
