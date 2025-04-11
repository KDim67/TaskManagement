package hua.dit.taskmanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

// SQLite database helper class for managing task database creation and version management
public class TaskDbHelper extends SQLiteOpenHelper {
    // Database version number
    public static final int DB_VERSION = 1;

    // SQL command for creating the tasks table with all required fields
    public static final String CREATE_DB_TABLE =
            "CREATE TABLE IF NOT EXISTS tasks ( " +
                    "uid INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "short_name TEXT NOT NULL, " +
                    "description TEXT, " +
                    "start_time INTEGER, " +
                    "duration_hours INTEGER, " +
                    "location TEXT, " +
                    "status TEXT NOT NULL" +
                    ");";

    // SQL command for dropping the tasks table
    public static final String DROP_DB_TABLE =
            "DROP TABLE IF EXISTS tasks;";

    // Constructor initializes database with provided context and name
    public TaskDbHelper(@Nullable Context context, @Nullable String name) {
        super(context, name, null, DB_VERSION);
    }

    // Called when database is created for the first time
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_TABLE);
    }

    // Called when database needs to be upgraded to a new version
    // Current implementation drops and recreates the table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DB_TABLE);
        onCreate(db);
    }
}
