package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * The class that handles the implementation of the local storage must implement this interface.
 */
@DebugLog
interface TaskListStateManager {
    void saveTasks(@NonNull final List<Task> taskList);

    void loadTasks();

    void setDataSetChangedListener(DataSetChangedListener listener);
}
