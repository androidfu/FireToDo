package com.chscodecamp.android.firetodo;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import hugo.weaving.DebugLog;

/**
 * The class that handles the implementation of the local storage must implement this interface.
 */
@DebugLog
interface TaskListStateManager {

    void onUpgrade(@NonNull final Context context, final int newVersion, final int oldVersion);

    void saveTasks(@NonNull final List<Task> taskList);

    void loadTasks();

    void setDataSetChangedListener(@NonNull final DataSetChangedListener listener);
}
