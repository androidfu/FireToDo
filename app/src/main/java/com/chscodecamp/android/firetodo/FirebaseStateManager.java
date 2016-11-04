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
class FirebaseStateManager implements TaskListStateManager {

    private static final String TAG = FirebaseStateManager.class.getSimpleName();
    private static final String TASKS = "tasks";
    private final DatabaseReference databaseReference;
    private DataSetChangedListener dataSetChangedListener;

    FirebaseStateManager() {
        final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.setPersistenceEnabled(true);
        databaseReference = firebaseDatabase.getReference(TASKS);
    }

    @Override
    public void saveTasks(@NonNull List<Task> taskList) {
        databaseReference.setValue(taskList);
    }

    @Override
    public void loadTasks() {
        final List<Task> taskList = new ArrayList<>();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.i(TAG, String.format("%s", snapshot.getValue().toString()));
                    if (snapshot.getValue(Task.class) != null) {
                        taskList.add(snapshot.getValue(Task.class));
                    }
                }
                if (taskList.isEmpty()) {
                    Task task = new Task("Start Learning Android!");
                    task.setCompleted(true);
                    taskList.add(task);
                    task = new Task("Keep Learning Android!");
                    taskList.add(task);
                }
                if (dataSetChangedListener != null) {
                    dataSetChangedListener.onDataSetChanged(taskList);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });
    }

    @Override
    public void setDataSetChangedListener(DataSetChangedListener dataSetChangedListener) {
        this.dataSetChangedListener = dataSetChangedListener;
    }
}
