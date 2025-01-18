package hua.dit.taskmanagement.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "tasks")
public class Task {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "short_name")
    private String shortName;

    @ColumnInfo(name = "description")
    private String description;

    @ColumnInfo(name = "start_time")
    private Date startTime;

    @ColumnInfo(name = "duration_hours")
    private Integer durationHours;

    @ColumnInfo(name = "location")
    private String location;

    @ColumnInfo(name = "status")
    private String status;

    public Task() {}

    public Task(String shortName, String description, Date startTime, Integer durationHours, String location) {
        this.shortName = shortName;
        this.description = description;
        this.startTime = startTime;
        this.durationHours = durationHours;
        this.location = location;
        this.status = "recorded";
    }

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