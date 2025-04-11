package hua.dit.taskmanagement;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import hua.dit.taskmanagement.fragments.CreateTaskFragment;
import hua.dit.taskmanagement.fragments.TaskListFragment;
import hua.dit.taskmanagement.fragments.TestProviderFragment;
import hua.dit.taskmanagement.workers.TaskStatusCheckWorker;

import java.util.concurrent.TimeUnit;

// Main activity of the task management application.
// Handles navigation between fragments and schedules periodic task status checks.
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Schedule periodic task status checks
        scheduleTaskStatusCheck();

        // Set up bottom navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(navListener);

        // Set initial fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new TaskListFragment())
                .commit();
    }

    // Schedules a periodic worker to check task statuses every hour.
    // Uses WorkManager to handle the background work scheduling.
    private void scheduleTaskStatusCheck() {
        WorkManager workManager = WorkManager.getInstance(this);
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(TaskStatusCheckWorker.class, 1, TimeUnit.HOURS)
                .build();
        workManager.enqueueUniquePeriodicWork("task_status_check", ExistingPeriodicWorkPolicy.UPDATE, periodicWorkRequest);
    }

    // Navigation listener that handles switching between fragments based on
    // bottom navigation item selection.
    private BottomNavigationView.OnItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;
        int itemId = item.getItemId();

        // Determine which fragment to show based on selected navigation item
        if (itemId == R.id.nav_tasks) {
            selectedFragment = new TaskListFragment();
        } else if (itemId == R.id.nav_create) {
            selectedFragment = new CreateTaskFragment();
        } else if (itemId == R.id.nav_test_provider) {
            selectedFragment = new TestProviderFragment();
        }

        // Replace current fragment with selected fragment
        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }

        return true;
    };
}