package hua.dit.taskmanagement.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.room.Room;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import hua.dit.taskmanagement.database.TaskDatabase;
import hua.dit.taskmanagement.entities.Task;

//ContentProvider implementation for Task entities
//Provides CRUD operations for tasks through a content provider interface
public class TaskContentProvider extends ContentProvider {
    // Logging tag for this class
    private static final String TAG = "TaskContentProvider";

    // Content provider authority and path constants
    private static final String AUTHORITY = "hua.dit.taskmanagement.provider";
    private static final String PATH = "tasks";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    // URI matcher codes for different types of requests
    private static final int TASKS = 1; // Code for operations on all tasks
    private static final int TASK_ID = 2; // Code for operations on a specific task

    // URI matcher for determining the type of request
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        // Register URI patterns with their corresponding codes
        uriMatcher.addURI(AUTHORITY, PATH, TASKS);
        uriMatcher.addURI(AUTHORITY, PATH + "/#", TASK_ID);
    }

    // Database instance
    private TaskDatabase db;

    // Initializes the content provider
    @Override
    public boolean onCreate() {
        // Initialize Room database
        db = Room.databaseBuilder(getContext(),
                        TaskDatabase.class,
                        "task_database")
                .build();
        return true;
    }

    // Handles insertion of new tasks
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // Verify that the URI is valid for insertion
        if (uriMatcher.match(uri) != TASKS) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        // Create new Task object from ContentValues
        Task task = new Task();
        task.setShortName(values.getAsString("short_name"));
        task.setDescription(values.getAsString("description"));

        // Parse and set the start time if provided
        String startTimeStr = values.getAsString("start_time");
        if (startTimeStr != null) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date startTime = dateFormat.parse(startTimeStr);
                task.setStartTime(startTime);
            } catch (ParseException e) {
                throw new IllegalArgumentException("Invalid date format. Use yyyy-MM-dd HH:mm:ss");
            }
        }

        // Set remaining task properties
        task.setDurationHours(values.getAsInteger("duration_hours"));
        task.setLocation(values.getAsString("location"));
        task.setStatus(values.getAsString("status"));

        try {
            // Insert task and get generated ID
            long id = db.taskDao().insert(task);
            if (id > 0) {
                Uri itemUri = ContentUris.withAppendedId(uri, id);
                getContext().getContentResolver().notifyChange(itemUri, null);
                return itemUri;
            }
            throw new android.database.SQLException("Failed to insert row into " + uri);
        } catch (Exception e) {
            throw new android.database.SQLException("Error inserting row: " + e.getMessage());
        }
    }

    // Handles querying tasks
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        try {
            Cursor cursor;
            // Determine query type based on URI
            switch (uriMatcher.match(uri)) {
                case TASKS:
                    // Query all tasks
                    cursor = db.taskDao().getAllTasksCursor();
                    break;
                case TASK_ID:
                    // Query specific task by ID
                    cursor = db.taskDao().getTaskByIdCursor(ContentUris.parseId(uri));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
            // Register cursor for URI notifications
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        } catch (Exception e) {
            throw new android.database.SQLException("Error querying database: " + e.getMessage());
        }
    }

    // Handles updating existing tasks
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        try {
            switch (uriMatcher.match(uri)) {
                case TASK_ID:
                    // Get task ID from URI
                    long taskId = ContentUris.parseId(uri);
                    Task existingTask = db.taskDao().getTaskById((int)taskId);
                    if (existingTask == null) {
                        return 0;
                    }

                    // Update task properties if provided in ContentValues
                    if (values.containsKey("short_name")) {
                        existingTask.setShortName(values.getAsString("short_name"));
                    }
                    if (values.containsKey("description")) {
                        existingTask.setDescription(values.getAsString("description"));
                    }
                    if (values.containsKey("status")) {
                        existingTask.setStatus(values.getAsString("status"));
                    }
                    if (values.containsKey("location")) {
                        existingTask.setLocation(values.getAsString("location"));
                    }

                    // Perform update and notify observers
                    int count = db.taskDao().update(existingTask);
                    if (count > 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                    }
                    return count;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } catch (Exception e) {
            throw new android.database.SQLException("Error updating database: " + e.getMessage());
        }
    }

    // Handles deleting tasks
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        try {
            switch (uriMatcher.match(uri)) {
                case TASK_ID:
                    // Delete task by ID and notify observers if successful
                    int count = db.taskDao().deleteById(ContentUris.parseId(uri));
                    if (count > 0) {
                        getContext().getContentResolver().notifyChange(uri, null);
                    }
                    return count;
                default:
                    throw new IllegalArgumentException("Unknown URI: " + uri);
            }
        } catch (Exception e) {
            throw new android.database.SQLException("Error deleting from database: " + e.getMessage());
        }
    }

    // Returns MIME type for tasks
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TASKS:
                // MIME type for multiple tasks
                return "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH;
            case TASK_ID:
                // MIME type for a single task
                return "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
