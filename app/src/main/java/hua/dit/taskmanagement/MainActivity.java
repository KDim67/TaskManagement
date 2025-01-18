package hua.dit.taskmanagement;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import hua.dit.taskmanagement.fragments.CreateTaskFragment;
import hua.dit.taskmanagement.fragments.TaskListFragment;
import hua.dit.taskmanagement.workers.TaskStatusCheckWorker;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scheduleTaskStatusCheck();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TaskListFragment())
                .commit();
    }

    private void scheduleTaskStatusCheck() {
        WorkManager workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(TaskStatusCheckWorker.class, 1, TimeUnit.HOURS)
                .build();
        workManager.enqueueUniquePeriodicWork("task_status_check", ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest);
    }

    private BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        if (itemId == R.id.nav_tasks) {
            selectedFragment = new TaskListFragment();
        } else if (itemId == R.id.nav_create) {
            selectedFragment = new CreateTaskFragment();
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        return true;
    };
}