package com.chscodecamp.android.firetodo;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
class FirebaseStateManager extends BaseStateManager {

    private static final String TAG = FirebaseStateManager.class.getSimpleName();
    private final DatabaseReference databaseReference;

    FirebaseStateManager(String uniqueId) {

        /**
         * Get an instance of the FirebaseDatabase and set it to persist data locally so the app
         * may be used even if network connectivity is not present.
         */
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);

        /**
         * Use our Application ID as the unique identifier for our list of tasks.  This will allow
         * every user to have their own list of tasks but is not a perfect solution as it will only
         * allow you to have unique tasks per device rather than unique to a user.
         *
         * We'll leave it up to you to handle users/authentication such that you can get to your
         * tasks regardless of which device you're on.
         */
        databaseReference = firebaseDatabase.getReference(uniqueId);
    }

    /**
     * Save our tasks to our long term storage solution.
     *
     * @param taskList the list of Tasks to be saved.
     */
    @Override
    public void saveTasks(@NonNull List<Task> taskList) {
        databaseReference.setValue(taskList);
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
         * We only care about listening for a single update on this database when the loadTasks()
         * method is called.  addListenerForSingleValueEvent() will run once then remove the
         * listener.
         */
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                /**
                 * For each Task in our saved Tasks ...
                 */
                for (DataSnapshot taskDbEntry : dataSnapshot.getChildren()) {

                    /**
                     * If getValue() results in a Task then add it to the taskList.
                     */
                    if (taskDbEntry.getValue(Task.class) != null) {
                        taskList.add(taskDbEntry.getValue(Task.class));
                    }
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

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }

        });
    }
}
