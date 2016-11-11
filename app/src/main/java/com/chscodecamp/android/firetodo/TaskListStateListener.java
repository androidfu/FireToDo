package com.chscodecamp.android.firetodo;

/**
 * The interface that will be called when our task list is updated such that we can notify our adapter.
 */
interface TaskListStateListener {
    void onTaskListUpdated();
}
