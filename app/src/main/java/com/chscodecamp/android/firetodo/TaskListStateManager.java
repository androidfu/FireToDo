package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;

import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
interface TaskListStateManager {
    void saveTasks(@NonNull final List<Task> taskList);

    void loadTasks();

    void setDataSetChangedListener(DataSetChangedListener listener);
}
