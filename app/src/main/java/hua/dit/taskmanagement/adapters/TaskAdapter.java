package hua.dit.taskmanagement.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    private List<Task> tasks = new ArrayList<>();
    private OnTaskClickListener listener;
    private OnTaskLongClickListener longClickListener;

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskLongClickListener {
        void onTaskLongClick(Task task);
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.listener = listener;
    }

    public void setOnTaskLongClickListener(OnTaskLongClickListener listener) {
        this.longClickListener = listener;
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_item, parent, false);
        return new TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TaskViewHolder holder, int position) {
        Task task = tasks.get(position);
        holder.textViewTitle.setText(task.getShortName());
        holder.textViewStatus.setText(task.getStatus());
        holder.textViewTime.setText(task.getStartTime().toString());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onTaskLongClick(task);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
        notifyDataSetChanged();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewTitle;
        private TextView textViewStatus;
        private TextView textViewTime;

        public TaskViewHolder(View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.text_view_title);
            textViewStatus = itemView.findViewById(R.id.text_view_status);
            textViewTime = itemView.findViewById(R.id.text_view_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onTaskClick(tasks.get(position));
                    }
                }
            });
        }
    }
}