package hua.dit.taskmanagement.converters;

import androidx.room.TypeConverter;
import java.util.Date;

// Converter class for handling Date objects in Room database
public class TaskConverters {
    // Converts Date object to Long timestamp for database storage
    @TypeConverter
    public Long dateToLong(Date date) {
        return (date != null) ? date.getTime() : null;
    }

    // Converts Long timestamp from database back to Date object
    @TypeConverter
    public Date longToDate(Long longval) {
        return (longval != null) ? new Date(longval) : null;
    }
}