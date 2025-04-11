package hua.dit.taskmanagement;

import android.app.Application;
import android.util.Log;
import hua.dit.taskmanagement.repositories.TaskRepositoryManager;
import java.io.IOException;

// Custom Application class that handles initialization and cleanup
// of the TaskRepositoryManager singleton throughout the app lifecycle.
public class TaskManagementApplication extends Application {
    private static final String TAG = "TaskManagementApp";
    private TaskRepositoryManager repositoryManager;

    // Initializes the TaskRepositoryManager singleton when the application starts.
    // This ensures the repository is available throughout the app's lifecycle.
    @Override
    public void onCreate() {
        super.onCreate();
        repositoryManager = TaskRepositoryManager.getInstance(this);
    }

    // Performs cleanup by closing the TaskRepositoryManager when the app terminates.
    @Override
    public void onTerminate() {
        super.onTerminate();
        if (repositoryManager != null) {
            try {
                repositoryManager.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing TaskRepositoryManager", e);
            }
        }
    }
}
