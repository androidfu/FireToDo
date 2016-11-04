package com.chscodecamp.android.firetodo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
class SharedPreferenceStateManager implements TaskListStateManager {

    private static final String SAVED_TASKS = "savedTasks";
    private SharedPreferences sharedPreferences;
    private DataSetChangedListener listener;

    SharedPreferenceStateManager(Application application) {
        sharedPreferences = application.getSharedPreferences(application.getPackageName(), Context.MODE_PRIVATE);
    }

    @SuppressLint("CommitPrefEdits")
    public void saveTasks(@NonNull List<Task> taskList) {
        sharedPreferences.edit().putString(SAVED_TASKS, new Gson().toJson(taskList)).commit();
    }

    public void loadTasks() {
        Task[] savedTasks = new Gson().fromJson(sharedPreferences.getString(SAVED_TASKS, null), Task[].class);
        if (savedTasks != null && savedTasks.length > 0) {
            if (listener != null) {
                listener.onDataSetChanged(Arrays.asList(savedTasks));
            }
        }
    }

    @Override
    public void setDataSetChangedListener(DataSetChangedListener listener) {
        this.listener = listener;
    }
}
