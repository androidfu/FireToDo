package com.chscodecamp.android.firetodo;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.CallSuper;
import android.support.annotation.IntRange;
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
    private static final String KEY_PREFS_DATA_SCHEMA_VERSION = "data_schema_version";
    DataSetChangedListener dataSetChangedListener;
    List<Task> tasksFromUpgrade = null;
    private int oldVersion;

    BaseStateManager(@NonNull final Context context, final int newVersion) {
        if (isUpgrade(context, newVersion)) {
            this.onUpgrade(context, newVersion, this.oldVersion);
        }
    }

    /**
     * Compare the schema version being passed in to the version we have stored in SharedPreferences
     *
     * @param context    our application context
     * @param newVersion the schema version number
     * @return true if the version number provided is greater than the version number stored
     */
    private boolean isUpgrade(@NonNull final Context context, final int newVersion) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(FireToDoApplication.class.getCanonicalName(), Context.MODE_PRIVATE);

        /**
         * Try to get the schema version from SharedPreferences and if it does not exist then return -1.
         */
        this.oldVersion = sharedPreferences.getInt(KEY_PREFS_DATA_SCHEMA_VERSION, -1);

        /**
         * If our result from SharedPreferences was -1 then we know we know there are no existing
         * tasks stored.  Set the versions equal to each other.
         */
        if (this.oldVersion == -1) {
            this.oldVersion = newVersion;
        }

        /**
         * We do not currently implement a downgrade path.  There would be no real-world way to do this via
         * the Google Play Store so this is largely a developer precaution to keep you from getting your data
         * into a weird state.
         */
        if (newVersion < this.oldVersion) {
            throw new RuntimeException(context.getString(R.string.error_msg_downgrade_not_allowed));
        }

        /**
         * Store the schema version number in SharedPreferences.  Use self-documenting constants for preference
         * keys and other similar pieces of data.  Don't forget to call .apply() or your new value
         * will never be saved ;)
         */
        sharedPreferences.edit().putInt(KEY_PREFS_DATA_SCHEMA_VERSION, newVersion).apply();

        /**
         * If the current schema version is greater than the version we had stored in SharedPreferences
         * then this is an upgrade.
         */
        return newVersion > this.oldVersion;
    }

    /**
     * Something about our data schema changed or we changed how the data is stored and we don't
     * want to lose our user's data during the transition.
     *
     * @param context    our application context
     * @param newVersion the new version number for our data
     */
    @Override
    public void onUpgrade(@NonNull final Context context, final @IntRange(from = 1, to = Integer.MAX_VALUE) int newVersion, final @IntRange(from = 1, to = Integer.MAX_VALUE) int oldVersion) {

        /**
         * From 1 to 2 ...
         */
        if (this.oldVersion < 2) {
            /**
             * Our first version should have been stored in Shared Preferences since this application
             * is a continuation of our BetterToDo class.
             */
            final SharedPreferenceStateManager sharedPreferenceStateManager = new SharedPreferenceStateManager(context, newVersion);

            /**
             * We need to hold a reference to the tasks because we haven't finished initializing everything.
             */
            sharedPreferenceStateManager.setDataSetChangedListener(new DataSetChangedListener() {
                @Override
                public void onDataSetChanged(@NonNull final List<Task> taskList) {
                    setTasks(taskList);

                    /**
                     * Now that we have an in-memory copy of our tasks go ahead and clean up the
                     * copy in SharedPreferences.
                     */
                    sharedPreferenceStateManager.deleteAllTasks();
                }
            });
            sharedPreferenceStateManager.loadTasks();
        }

        /**
         * From 2 to 3 ...
         */
        //noinspection StatementWithEmptyBody
        if (this.oldVersion < 3) {
            /**
             * Notice that these version checks are _not_ chained together.  That's because we don't
             * know which version the user will be coming from.  In this way we can ensure that the
             * entire upgrade path will be supported.
             */
        }
    }

    /**
     * We need to hold on to an in-memory copy of tasks if we're coming from an upgrade because
     * everything isn't finished initializing.
     *
     * @param taskList the tasks from a previous version of the app that required a data upgrade
     */
    private void setTasks(@NonNull final List<Task> taskList) {
        this.tasksFromUpgrade = taskList;
    }

    /**
     * Allow our Activity to tell the state manager class that is wants to be notified when the
     * data changes.
     *
     * @param dataSetChangedListener the listener we will notify when the data changes.
     */
    @Override
    public void setDataSetChangedListener(@NonNull final DataSetChangedListener dataSetChangedListener) {
        this.dataSetChangedListener = dataSetChangedListener;
    }

    /**
     * Call to set see our Task list with items.
     *
     * @param taskList the task list we will modify with our default entries
     */
    @CallSuper
    void createDefaultEntries(@NonNull final List<Task> taskList) {
        Task task = new Task("Start Learning Android!");
        task.setCompleted(true);
        taskList.add(task);
        task = new Task("Keep Learning Android!");
        task.setCompleted(true);
        taskList.add(task);
    }
}
