package hua.dit.taskmanagement.repositories;

import android.app.Application;

public class TaskRepositoryManager {
    private static TaskRepository instance;

    public static synchronized TaskRepository getInstance(Application application) {
        if (instance == null) {
            synchronized (TaskRepositoryManager.class){
                if(instance == null) {
                    instance = new TaskRepository(application);
                }
            }
        }
        return instance;
    }
}