package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * This interface allows our classes that extend TaskListStateManager to notify the activity holding
 * the adapter that the data has changed.
 */
interface DataSetChangedListener {
    void onDataSetChanged(@NonNull final List<Task> taskList);
}
