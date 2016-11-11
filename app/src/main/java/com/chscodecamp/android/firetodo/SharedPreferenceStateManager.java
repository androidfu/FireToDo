package com.chscodecamp.android.firetodo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
class SharedPreferenceStateManager extends BaseStateManager {

    private static final String SAVED_TASKS = "savedTasks";
    private SharedPreferences sharedPreferences;

    SharedPreferenceStateManager(@NonNull Context context, int dataSchemaVersion) {
        super(context, dataSchemaVersion);
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * Save our tasks to our long term storage solution.
     *
     * @param taskList the list of Tasks to be saved.
     */
    @SuppressLint("CommitPrefEdits")
    @Override
    public void saveTasks(@NonNull List<Task> taskList) {
        sharedPreferences.edit().putString(SAVED_TASKS, new Gson().toJson(taskList)).commit();
    }

    /**
     * Get our tasks from our long term storage solution.
     */
    @Override
    public void loadTasks() {

        /**
         * Create an empty list of tasks that we will pass back.  This ensures that we're never
         * working with a null task list.
         */
        final List<Task> taskList = new ArrayList<>();

        /**
         * Get our Tasks from shared preferences and store them in an array.
         */
        Task[] savedTasks = new Gson().fromJson(sharedPreferences.getString(SAVED_TASKS, null), Task[].class);

        /**
         * If we managed to get tasks from shared preferences then add them to our task list.
         */
        if (savedTasks != null && savedTasks.length > 0) {
            taskList.addAll(Arrays.asList(savedTasks));
        }

        /**
         * If the list is empty after processing all database entries then add default
         * tasks so our user has something to see when they launch the app.
         */
        if (taskList.isEmpty()) {
            createDefaultEntries(taskList);
        }

        /**
         * After this method completes we will have update our taskList and we need to tell
         * the adapter to refresh its dataset so they can be displayed on the screen.
         */
        if (dataSetChangedListener != null) {
            dataSetChangedListener.onDataSetChanged(taskList);
        }
    }

    void deleteAllTasks() {
        sharedPreferences.edit().remove(SAVED_TASKS).apply();
    }
}
