package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
class TaskManager implements DataSetChangedListener {
    private static TaskManager instance;
    private final List<Task> taskList = new ArrayList<>();
    private final TaskListStateManager taskListStateManager;
    private TaskListStateListener taskListStateListener;

    private TaskManager(@NonNull final TaskListStateManager taskListStateManager) {
        this.taskListStateManager = taskListStateManager;
        this.taskListStateManager.setDataSetChangedListener(this);
    }

    static void init(@NonNull final TaskListStateManager taskListStateManager) {
        if (instance == null) {
            instance = new TaskManager(taskListStateManager);
        }
        instance.taskList.clear();
        instance.taskListStateManager.loadTasks();
    }

    @NonNull
    static TaskManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("Call TaskManager.init() first.");
        }
        return instance;
    }

    @Override
    public void onDataSetChanged(@NonNull final List<Task> taskList) {
        this.taskList.clear();
        this.taskList.addAll(taskList);
        if (taskListStateListener != null) {
            taskListStateListener.onTaskListUpdated();
        }
    }

    @NonNull
    List<Task> getTasks() {
        return taskList;
    }

    void addTask(@NonNull final Task task) {
        taskList.add(task);
        saveTasks();
    }

    void updateTask(@NonNull final Task task) {
        taskList.set(taskList.indexOf(task), task);
        saveTasks();
    }

    void setTaskListStateListener(@NonNull final TaskListStateListener taskListStateListener) {
        this.taskListStateListener = taskListStateListener;
    }

    private void saveTasks() {
        taskListStateManager.saveTasks(taskList);
    }
}
