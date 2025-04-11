package hua.dit.taskmanagement.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

// Entity class representing a task in the database
@Entity(tableName = "tasks")
public class Task {
    // Unique identifier for the task, automatically generated
    @PrimaryKey(autoGenerate = true)
    private int uid;

    // Short name of the task
    @ColumnInfo(name = "short_name")
    private String shortName;

    // Detailed description of the task
    @ColumnInfo(name = "description")
    private String description;

    // Start time of the task
    @ColumnInfo(name = "start_time")
    private Date startTime;

    // Duration of the task in hours
    @ColumnInfo(name = "duration_hours")
    private Integer durationHours;

    // Location where the task takes place
    @ColumnInfo(name = "location")
    private String location;

    // Current status of the task
    @ColumnInfo(name = "status")
    private String status;

    // Default constructor required by Room
    public Task() {}

    // Constructor for creating a new task with initial values
    // Status is automatically set to "recorded"
    public Task(String shortName, String description, Date startTime, Integer durationHours, String location) {
        this.shortName = shortName;
        this.description = description;
        this.startTime = startTime;
        this.durationHours = durationHours;
        this.location = location;
        this.status = "recorded";
    }

    // Getters and setters
    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getDurationHours() {
        return durationHours;
    }

    public void setDurationHours(Integer durationHours) {
        this.durationHours = durationHours;
    }

    public String getLocation() {
        return location != null ? location : "";
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Returns a string representation of the Task object
    @Override
    public String toString() {
        return "TaskEntity{" +
                "uid=" + uid +
                ", shortName='" + shortName + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", durationHours=" + durationHours +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}