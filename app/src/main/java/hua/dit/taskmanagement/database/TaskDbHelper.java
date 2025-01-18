package hua.dit.taskmanagement.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class TaskDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;

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

    public static final String DROP_DB_TABLE =
            "DROP TABLE IF EXISTS tasks;";

    public TaskDbHelper(@Nullable Context context, @Nullable String name) {
        super(context, name, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_DB_TABLE);
        onCreate(db);
    }
}
