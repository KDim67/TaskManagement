package hua.dit.taskmanagement.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import hua.dit.taskmanagement.entities.Task;

// Utility class for exporting tasks to HTML format
public class TaskExporter {
    // Logging tag for debugging purposes
    private static final String TAG = "TaskExporter";

    // Date formatter for consistent date formatting throughout the class
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    // Exports a list of tasks to an HTML file in the Downloads directory
    public static File exportTasksToHtml(Context context, List<Task> tasks) throws IOException {
        //Generate unique filename using timestamp
        String fileName = "incomplete_tasks_" + System.currentTimeMillis() + ".html";

        // Build HTML content with CSS styling
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n")
                .append("<html>\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<title>Incomplete Tasks</title>\n")
                .append("<style>\n")
                .append("body { font-family: Arial, sans-serif; margin: 20px; }\n")
                .append("table { border-collapse: collapse; width: 100%; }\n")
                .append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }\n")
                .append("th { background-color: #f2f2f2; }\n")
                .append("tr:nth-child(even) { background-color: #f9f9f9; }\n")
                .append("</style>\n</head>\n<body>\n")
                .append("<h1>Incomplete Tasks</h1>\n")
                .append("<table>\n")
                .append("<tr><th>Name</th><th>Description</th><th>Start Time</th>")
                .append("<th>Duration (Hours)</th><th>Location</th><th>Status</th></tr>\n");

        // Iterate through tasks and add them to the HTML table
        for (Task task : tasks) {
            html.append("<tr>")
                    .append("<td>").append(escapeHtml(task.getShortName())).append("</td>")
                    .append("<td>").append(escapeHtml(task.getDescription())).append("</td>")
                    .append("<td>").append(dateFormat.format(task.getStartTime())).append("</td>")
                    .append("<td>").append(task.getDurationHours()).append("</td>")
                    .append("<td>").append(escapeHtml(task.getLocation())).append("</td>")
                    .append("<td>").append(escapeHtml(task.getStatus())).append("</td>")
                    .append("</tr>\n");
        }

        html.append("</table>\n</body>\n</html>");

        try {
            // Set up ContentValues for file creation
            ContentValues cv = new ContentValues();
            cv.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            cv.put(MediaStore.MediaColumns.MIME_TYPE, "text/html");
            cv.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            // Get content resolver and create file
            ContentResolver resolver = context.getContentResolver();
            Uri fileUri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv);

            if (fileUri != null) {
                // Write HTML content to file
                try (OutputStream os = resolver.openOutputStream(fileUri, "w")) {
                    if (os != null) {
                        os.write(html.toString().getBytes());
                        os.flush();
                        Log.d(TAG, "File successfully created!");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating file: " + e.getMessage());
            throw new IOException("Failed to create file: " + e.getMessage());
        }

        return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
    }

    // Escapes special HTML characters to prevent XSS attacks
    // This is done by replacing for example < with its correspondent &lt which itself stands for less than.
    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#039;");
    }
}
