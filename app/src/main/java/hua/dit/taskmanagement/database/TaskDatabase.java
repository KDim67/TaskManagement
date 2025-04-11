package hua.dit.taskmanagement.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import hua.dit.taskmanagement.converters.TaskConverters;
import hua.dit.taskmanagement.dao.TaskDao;
import hua.dit.taskmanagement.entities.Task;

// Database configuration: defines Task as the only entity, sets version to 1, and enables schema export
@Database(entities = {Task.class}, version = 1, exportSchema = true)
// Registers type converters for custom data type handling (Date conversions)
@TypeConverters({TaskConverters.class})
public abstract class TaskDatabase extends RoomDatabase {
    // Abstract method to access the Task DAO
    public abstract TaskDao taskDao();
}