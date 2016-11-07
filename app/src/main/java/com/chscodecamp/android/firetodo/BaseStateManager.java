package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * Making an abstract base class that implements TaskListStateManager ensures that each class that
 * extends this base class will have a default implementation of setDataSetChangedListener() as well
 * as the same createDefaultEntries() method.  They can still, however, be overridden by the
 * concrete implementations.
 */
@DebugLog
abstract class BaseStateManager implements TaskListStateManager {
    DataSetChangedListener dataSetChangedListener;

    @Override
    abstract public void saveTasks(@NonNull List<Task> taskList);

    @Override
    abstract public void loadTasks();

    /**
     * Allow our Activity to tell the state manager class that is wants to be notified when the
     * data changes.
     *
     * @param dataSetChangedListener the listener we will notify when the data changes.
     */
    @Override
    public void setDataSetChangedListener(DataSetChangedListener dataSetChangedListener) {
        this.dataSetChangedListener = dataSetChangedListener;
    }

    /**
     * Call to set see our Task list with items.
     *
     * @param taskList the task list we will modify with our default entries
     */
    void createDefaultEntries(List<Task> taskList) {
        Task task = new Task("Start Learning Android!");
        task.setCompleted(true);
        taskList.add(task);
        task = new Task("Keep Learning Android!");
        taskList.add(task);
    }
}
