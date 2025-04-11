package hua.dit.taskmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;

// Adapter class for handling the display of Task items in a RecyclerView
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    // List to store task items
    private List<Task> tasks = new ArrayList<>();
    // Interface instances for handling click events
    private OnTaskClickListener clickListener;
    private OnTaskLongClickListener longClickListener;
    // Date formatter for consistent date display
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    // Creates new ViewHolder instances for the RecyclerView
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    // Binds data to the ViewHolder for display
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.idTextView.setText(String.format("ID: %d", currentTask.getUid()));
        holder.titleTextView.setText(currentTask.getShortName());

        // Handle date display with null check
        if (currentTask.getStartTime() != null) {
            holder.timeTextView.setText(dateFormat.format(currentTask.getStartTime()));
        } else {
            holder.timeTextView.setText("No date set");
        }

        // Set status text with null check
        String status = currentTask.getStatus() != null ? currentTask.getStatus() : "N/A";
        holder.statusTextView.setText(String.format("Status: %s", status));

        // Set text color based on task status
        int textColor;
        switch (status) {
            case "expired":
                textColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark);
                break;
            case "in-progress":
                textColor = holder.itemView.getContext().getResources().getColor(android.R.color.holo_orange_dark);
                break;
            default:
                textColor = holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray);
                break;
        }
        holder.statusTextView.setTextColor(textColor);
    }

    // Returns the total number of items in the adapter
    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnTaskLongClickListener(OnTaskLongClickListener listener) {
        this.longClickListener = listener;
    }

    // ViewHolder class for caching view references
    class TaskViewHolder extends RecyclerView.ViewHolder {
        // View elements for displaying task information
        private TextView idTextView;
        private TextView titleTextView;
        private TextView timeTextView;
        private TextView statusTextView;

        // Initialize ViewHolder and set up click listeners
        public TaskViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.text_view_id);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            timeTextView = itemView.findViewById(R.id.text_view_time);
            statusTextView = itemView.findViewById(R.id.text_view_status);

            // Set up click listener for normal clicks
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onTaskClick(tasks.get(position));
                }
            });

            // Set up click listener for long clicks
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener.onTaskLongClick(tasks.get(position));
                    return true;
                }
                return false;
            });
        }
    }

    // Interface for handling normal click events
    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    // Interface for handling long click events
    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }
}