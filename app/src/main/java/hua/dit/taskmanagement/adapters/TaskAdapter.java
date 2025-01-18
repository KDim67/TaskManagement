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

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener clickListener;
    private OnTaskLongClickListener longClickListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.idTextView.setText(String.format("ID: %d", currentTask.getUid()));
        holder.titleTextView.setText(currentTask.getShortName());
        holder.timeTextView.setText(dateFormat.format(currentTask.getStartTime()));
        holder.statusTextView.setText(String.format("Status: %s", currentTask.getStatus()));

        int textColor;
        switch (currentTask.getStatus()) {
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

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView idTextView;
        private TextView titleTextView;
        private TextView timeTextView;
        private TextView statusTextView;

        public TaskViewHolder(View itemView) {
            super(itemView);
            idTextView = itemView.findViewById(R.id.text_view_id);
            titleTextView = itemView.findViewById(R.id.text_view_title);
            timeTextView = itemView.findViewById(R.id.text_view_time);
            statusTextView = itemView.findViewById(R.id.text_view_status);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onTaskClick(tasks.get(position));
                }
            });

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

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }
}