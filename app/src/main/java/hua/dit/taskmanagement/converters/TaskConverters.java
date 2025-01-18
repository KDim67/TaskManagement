package hua.dit.taskmanagement.converters;

import androidx.room.TypeConverter;
import java.util.Date;

public class TaskConverters {
    @TypeConverter
    public Long dateToLong(Date date) {
        return (date != null) ? date.getTime() : null;
    }

    @TypeConverter
    public Date longToDate(Long longval) {
        return (longval != null) ? new Date(longval) : null;
    }
}