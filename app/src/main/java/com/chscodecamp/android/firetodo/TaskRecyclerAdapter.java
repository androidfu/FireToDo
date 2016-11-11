package com.chscodecamp.android.firetodo;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
class TaskRecyclerAdapter extends RecyclerView.Adapter<TaskRecyclerAdapter.TaskViewHolder> {

    private final List<Task> incompleteTasks = new ArrayList<>();
    private boolean hideCompletedTasks;
    private List<Task> allTasks;
    private Callback callback;

    TaskRecyclerAdapter(List<Task> tasks, boolean hideCompletedTasks, Callback callback) {
        this.allTasks = tasks;
        this.hideCompletedTasks = hideCompletedTasks;
        this.callback = callback;
        this.updateIncompleteTasks();
    }

    @Override
    public TaskViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new TaskViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_todo_item, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(final TaskViewHolder holder, int position) {

        final Task task = hideCompletedTasks ? incompleteTasks.get(position) : allTasks.get(position);

        holder.itemName.setText(task.getTitle());
        setStrikethrough(task.getCompleted(), holder.itemName);

        holder.checkBox.setChecked(task.getCompleted());
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                task.setCompleted(holder.checkBox.isChecked());
                setStrikethrough(task.getCompleted(), holder.itemName);
                if (callback != null) {
                    callback.onTaskUpdated(task);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return hideCompletedTasks ? incompleteTasks.size() : allTasks.size();
    }

    void setHideCompletedTasks(boolean hideCompletedTasks) {
        this.hideCompletedTasks = hideCompletedTasks;
        this.updateIncompleteTasks();
    }

    void updateIncompleteTasks() {
        incompleteTasks.clear();
        for (Task task : this.allTasks) {
            if (!task.getCompleted()) {
                incompleteTasks.add(task);
            }
        }
        this.notifyDataSetChanged();
    }

    private void setStrikethrough(boolean checked, TextView textView) {
        if (checked) {
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            textView.setPaintFlags(0);
        }
    }

    interface Callback {
        void onTaskUpdated(@NonNull Task task);
    }

    class TaskViewHolder extends RecyclerView.ViewHolder {
        private TextView itemName;
        private CheckBox checkBox;

        TaskViewHolder(View v) {
            super(v);
            itemName = (TextView) v.findViewById(R.id.item_name);
            checkBox = (CheckBox) v.findViewById(R.id.item_checkbox);
        }
    }
}