package hua.dit.taskmanagement.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import hua.dit.taskmanagement.converters.TaskConverters;
import hua.dit.taskmanagement.dao.TaskDao;
import hua.dit.taskmanagement.entities.Task;

@Database(entities = {Task.class}, version = 1, exportSchema = true)
@TypeConverters({TaskConverters.class})
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}