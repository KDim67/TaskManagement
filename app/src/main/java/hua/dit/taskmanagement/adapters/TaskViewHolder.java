package hua.dit.taskmanagement.adapters;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import hua.dit.taskmanagement.R;
import hua.dit.taskmanagement.entities.Task;

public class TaskViewHolder extends RecyclerView.ViewHolder {
    private final TextView textViewTitle;
    private final TextView textViewStatus;
    private final TextView textViewTime;
    private final View itemView;

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        this.itemView = itemView;
        textViewTitle = itemView.findViewById(R.id.text_view_title);
        textViewStatus = itemView.findViewById(R.id.text_view_status);
        textViewTime = itemView.findViewById(R.id.text_view_time);
    }

    public void bind(Task task, TaskAdapter.OnTaskClickListener clickListener, TaskAdapter.OnTaskLongClickListener longClickListener) {
        textViewTitle.setText(task.getShortName());
        textViewStatus.setText(task.getStatus());
        textViewTime.setText(task.getStartTime().toString());

        itemView.setOnClickListener(v -> {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION && clickListener != null) {
                clickListener.onTaskClick(task);
            }
        });

        itemView.setOnLongClickListener(v -> {
            int position = getBindingAdapterPosition();
            if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                longClickListener.onTaskLongClick(task);
                return true;
            }
            return false;
        });
    }
}
