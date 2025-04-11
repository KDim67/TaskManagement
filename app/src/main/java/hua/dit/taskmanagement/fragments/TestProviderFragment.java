package hua.dit.taskmanagement.fragments;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.provider.TaskContentProvider;


//Fragment class for testing ContentProvider operations
//Provides UI controls for CRUD operations on tasks
public class TestProviderFragment extends Fragment {
    // TextView to display operation results
    private TextView resultText;
    // Tracks the ID of the last inserted task
    private long lastInsertedId = -1;
    // Single thread executor for background operations
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    // Handler for posting updates to the main thread
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // Inflates the fragment layout and initializes UI components
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_test_provider, container, false);

        // Initialize TextView for displaying results
        resultText = view.findViewById(R.id.result_text);

        // Initialize buttons for CRUD operations
        Button insertButton = view.findViewById(R.id.insert_button);
        Button queryButton = view.findViewById(R.id.query_button);
        Button updateButton = view.findViewById(R.id.update_button);
        Button deleteButton = view.findViewById(R.id.delete_button);
        Button queryByIdButton = view.findViewById(R.id.query_by_id_button);

        // Set click listeners for all buttons
        insertButton.setOnClickListener(v -> executeInBackground(this::insertTestTask));
        queryButton.setOnClickListener(v -> executeInBackground(this::queryTasks));
        updateButton.setOnClickListener(v -> executeInBackground(this::updateLastTask));
        deleteButton.setOnClickListener(v -> executeInBackground(this::deleteLastTask));
        queryByIdButton.setOnClickListener(v -> executeInBackground(this::queryLastInsertedTask));

        return view;
    }

    // Executes a task on the background thread
    private void executeInBackground(Runnable task) {
        executor.execute(task);
    }

    // Updates the UI text on the main thread
    private void updateUI(String message) {
        mainHandler.post(() -> {
            if (isAdded()) {
                resultText.setText(message);
            }
        });
    }

    // Shows a toast message on the main thread
    private void showToast(String message) {
        mainHandler.post(() -> {
            if (isAdded()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Inserts a test task into the database using ContentProvider
    private void insertTestTask() {
        try {
            // Create ContentValues object with task data
            ContentValues values = new ContentValues();
            values.put("short_name", "Test Task");
            values.put("description", "This is a test task created via content provider");

            // Format current date-time for the task
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            values.put("start_time", dateFormat.format(new Date()));

            values.put("duration_hours", 2);
            values.put("location", "Test Location");
            values.put("status", "recorded");

            // Insert the task and get the URI
            Uri uri = requireContext().getContentResolver().insert(TaskContentProvider.CONTENT_URI, values);
            if (uri != null) {
                lastInsertedId = Long.parseLong(uri.getLastPathSegment());
                updateUI("Inserted task with ID: " + lastInsertedId);
                showToast("Task inserted successfully with ID: " + lastInsertedId);
            } else {
                showToast("Failed to insert task: No URI returned");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error inserting task: " + e.getMessage());
            updateUI("Error inserting task: " + e.getMessage());
        }
    }


    // Queries and displays all tasks in the database
    private void queryTasks() {
        try {
            // Query all tasks from the ContentProvider
            Cursor cursor = requireContext().getContentResolver().query(
                    TaskContentProvider.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );

            StringBuilder result = new StringBuilder();
            result.append("=== All Tasks (from both app and provider) ===\n\n");

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    do {
                        // Get column indices
                        int idIndex = cursor.getColumnIndex("uid");
                        int shortNameIndex = cursor.getColumnIndex("short_name");
                        int descriptionIndex = cursor.getColumnIndex("description");
                        int startTimeIndex = cursor.getColumnIndex("start_time");
                        int durationIndex = cursor.getColumnIndex("duration_hours");
                        int locationIndex = cursor.getColumnIndex("location");
                        int statusIndex = cursor.getColumnIndex("status");

                        // Build result string with task details
                        result.append("Task ID: ").append(idIndex >= 0 ? cursor.getInt(idIndex) : "N/A")
                                .append("\nShort Name: ").append(shortNameIndex >= 0 ? cursor.getString(shortNameIndex) : "N/A")
                                .append("\nDescription: ").append(descriptionIndex >= 0 ? cursor.getString(descriptionIndex) : "N/A")
                                .append("\nStart Time: ").append(startTimeIndex >= 0 ? cursor.getString(startTimeIndex) : "N/A")
                                .append("\nDuration: ").append(durationIndex >= 0 ? cursor.getInt(durationIndex) : "N/A")
                                .append(" hours")
                                .append("\nLocation: ").append(locationIndex >= 0 ? cursor.getString(locationIndex) : "N/A")
                                .append("\nStatus: ").append(statusIndex >= 0 ? cursor.getString(statusIndex) : "N/A")
                                .append("\n----------------------------------------\n");
                    } while (cursor.moveToNext());
                } else {
                    result.append("No tasks found in the database");
                }
                cursor.close();
            } else {
                result.append("Query returned null cursor");
            }
            updateUI(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error querying tasks: " + e.getMessage());
            updateUI("Error querying tasks: " + e.getMessage());
        }
    }

    // Updates the last inserted task through content provider with new values
    private void updateLastTask() {
        if (lastInsertedId == -1) {
            showToast("Please insert a task through the content provider first");
            return;
        }

        try {
            // Create URI for the specific task
            Uri taskUri = Uri.withAppendedPath(TaskContentProvider.CONTENT_URI, String.valueOf(lastInsertedId));
            Cursor cursor = requireContext().getContentResolver().query(taskUri, null, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                // Update task values
                ContentValues values = new ContentValues();
                values.put("short_name", "Updated Test Task");
                values.put("description", "This task was updated via content provider");
                values.put("status", "in-progress");

                // Perform update operation
                int count = requireContext().getContentResolver().update(taskUri, values, null, null);
                if (count > 0) {
                    updateUI("Successfully updated task with ID: " + lastInsertedId);
                    showToast("Task updated successfully");
                    queryLastInsertedTask();
                } else {
                    updateUI("Failed to update task");
                    showToast("Failed to update task");
                }
            } else {
                updateUI("Task not found");
                showToast("Task not found");
            }

            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Error updating task: " + e.getMessage());
            updateUI("Error updating task: " + e.getMessage());
        }
    }

    // Deletes the last inserted task through content provider
    private void deleteLastTask() {
        if (lastInsertedId == -1) {
            showToast("Please insert a task through the content provider first");
            return;
        }

        try {
            // Create URI for the specific task and delete it
            Uri taskUri = Uri.withAppendedPath(TaskContentProvider.CONTENT_URI, String.valueOf(lastInsertedId));
            int count = requireContext().getContentResolver().delete(taskUri, null, null);

            if (count > 0) {
                updateUI("Successfully deleted task with ID: " + lastInsertedId);
                lastInsertedId = -1;
            } else {
                updateUI("Failed to delete task");
            }
        } catch (Exception e) {
            showToast("Error: " + e.getMessage());
            updateUI("Error deleting task: " + e.getMessage());
        }
    }

    // Queries and displays details of the last inserted task through content provider
    private void queryLastInsertedTask() {
        if (lastInsertedId == -1) {
            showToast("Please insert a task through the content provider first");
            return;
        }

        StringBuilder result = new StringBuilder();
        try {
            // Create URI for the specific task and query it
            Uri taskUri = Uri.withAppendedPath(TaskContentProvider.CONTENT_URI, String.valueOf(lastInsertedId));
            Cursor cursor = requireContext().getContentResolver().query(
                    taskUri,
                    null,
                    null,
                    null,
                    null
            );

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    // Get column indices and build result string
                    int shortNameIndex = cursor.getColumnIndex("short_name");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int startTimeIndex = cursor.getColumnIndex("start_time");
                    int durationIndex = cursor.getColumnIndex("duration_hours");
                    int locationIndex = cursor.getColumnIndex("location");
                    int statusIndex = cursor.getColumnIndex("status");

                    result.append("Task ID: ").append(lastInsertedId)
                            .append("\nShort Name: ").append(shortNameIndex >= 0 ? cursor.getString(shortNameIndex) : "N/A")
                            .append("\nDescription: ").append(descriptionIndex >= 0 ? cursor.getString(descriptionIndex) : "N/A")
                            .append("\nStart Time: ").append(startTimeIndex >= 0 ? cursor.getString(startTimeIndex) : "N/A")
                            .append("\nDuration: ").append(durationIndex >= 0 ? cursor.getInt(durationIndex) : "N/A")
                            .append(" hours")
                            .append("\nLocation: ").append(locationIndex >= 0 ? cursor.getString(locationIndex) : "N/A")
                            .append("\nStatus: ").append(statusIndex >= 0 ? cursor.getString(statusIndex) : "N/A");
                } else {
                    result.append("Task not found");
                }
                cursor.close();
            } else {
                result.append("Query returned null cursor");
            }
        } catch (Exception e) {
            result.append("Error querying task: ").append(e.getMessage());
        }

        updateUI(result.toString());
    }

    // Cleanup executor service when fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}