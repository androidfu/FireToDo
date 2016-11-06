package com.chscodecamp.android.firetodo;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.UUID;

import hugo.weaving.DebugLog;

@DebugLog
public class FireToDoApplication extends Application {

    public static final String KEY_PREFS_APPLICATION_ID = "applicationId";

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * Init our TaskManager with a new instance of a FirebaseStateManager so we can store our
         * Tasks in the cloud!  The FirebaseStateManager takes a String in its constructor.  This
         * String will be used to uniquely identify "this" installation of the application so each
         * user's tasks can be stored independently of each others.
         */
        TaskManager.init(new FirebaseStateManager(this.getApplicationId()));
    }

    /**
     * If we do not already have an Application ID stored in SharedPreferences then create one and
     * save it for future use.  The Application ID will allow us to keep each user's Tasks separate,
     * but even though our tasks will be in the cloud this solution will not allow for multi-device
     * installations with access to the same Tasks.  That is for another class or for you to do on
     * your own time. ;)
     *
     * @return a String representing this Application's unique ID
     */
    @NonNull
    private String getApplicationId() {
        SharedPreferences sharedPreferences = this.getSharedPreferences(FireToDoApplication.class.getCanonicalName(), Context.MODE_PRIVATE);

        /**
         * Try to get the ID from SharedPreferences and if it does not exist then return null.
         */
        String applicationId = sharedPreferences.getString(KEY_PREFS_APPLICATION_ID, null);

        /**
         * If our result from SharedPreferences was null then we know we need to create a unique ID.
         * A UUID generated in this way isn't 100% guaranteed to be unique, but it's going to be the
         * best, easiest solution for our needs.
         */
        if (TextUtils.isEmpty(applicationId)) {
            applicationId = UUID.randomUUID().toString();

            /**
             * Store the unique ID in SharedPreferences.  Use self-documenting constants for preference
             * keys and other similar pieces of data.  Don't forget to call .apply() or your new value
             * will never be saved ;)
             */
            sharedPreferences.edit().putString(KEY_PREFS_APPLICATION_ID, applicationId).apply();
        }
        return applicationId;
    }
}
