package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Our Task helper class that removes all the implementation details of handling a Task from our
 * Activity.
 */
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

    /**
     * Provide our TaskManager a class that will handle the long term storage of our tasks.
     *
     * @param taskListStateManager the class that implements TaskListStateManager interface.
     */
    static void init(@NonNull final TaskListStateManager taskListStateManager) {
        if (instance == null) {
            instance = new TaskManager(taskListStateManager);
        }
        instance.taskList.clear();
        instance.taskListStateManager.loadTasks();
    }

    /**
     * Returns an instance of our TaskManager so the user doesn't have to call "new" all the time.
     *
     * @return TaskManager
     */
    @NonNull
    static TaskManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("Call TaskManager.init() first.");
        }
        return instance;
    }

    /**
     * Update our TaskList when our StateManager gives us a list of task and notify our Activity
     * so it can update the View.
     *
     * @param taskList the new list of tasks
     */
    @Override
    public void onDataSetChanged(@NonNull final List<Task> taskList) {
        this.taskList.clear();
        this.taskList.addAll(taskList);
        if (taskListStateListener != null) {
            taskListStateListener.onTaskListUpdated();
        }
    }

    /**
     * Get our list of tasks
     *
     * @return the Task List ;)
     */
    @NonNull
    List<Task> getTasks() {
        return taskList;
    }

    /**
     * Add a Task to our list and save it.
     *
     * @param task to be added/saved
     */
    void addTask(@NonNull final Task task) {
        taskList.add(task);
        saveTasks();
    }

    /**
     * Change the state of an existing task.
     *
     * @param task the task to be updated
     */
    void updateTask(@NonNull final Task task) {
        taskList.set(taskList.indexOf(task), task);
        saveTasks();
    }

    /**
     * The class that wishes to be notified when the list is updated.
     *
     * @param taskListStateListener class that implements TaskListStateListener
     */
    void setTaskListStateListener(@NonNull final TaskListStateListener taskListStateListener) {
        this.taskListStateListener = taskListStateListener;
    }

    /**
     * Save our tasks
     */
    private void saveTasks() {
        taskListStateManager.saveTasks(taskList);
    }
}
