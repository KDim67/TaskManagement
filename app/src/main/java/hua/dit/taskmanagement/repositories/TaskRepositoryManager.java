package hua.dit.taskmanagement.repositories;

import android.app.Application;
import java.io.Closeable;
import java.io.IOException;

// Singleton manager class for TaskRepository
// Ensures single instance of TaskRepository throughout the application
public class TaskRepositoryManager implements Closeable {
    // Singleton instance
    private static volatile TaskRepositoryManager instance;

    // The managed TaskRepository instance
    private final TaskRepository taskRepository;

    // Private constructor to prevent direct instantiation
    private TaskRepositoryManager(Application application) {
        this.taskRepository = new TaskRepository(application);
    }


    // Gets or creates the singleton instance
    public static TaskRepositoryManager getInstance(Application application) {
        if (instance == null) {
            synchronized (TaskRepositoryManager.class) {
                if (instance == null) {
                    instance = new TaskRepositoryManager(application);
                }
            }
        }
        return instance;
    }

    // Provides access to the managed TaskRepository
    public TaskRepository getTaskRepository() {
        return taskRepository;
    }

    //Closes and cleans up the manager instance
    //Implements Closeable interface
    @Override
    public void close() throws IOException {
        synchronized (TaskRepositoryManager.class) {
            if (instance != null) {
                if (taskRepository != null) {
                }
                instance = null;
            }
        }
    }
}