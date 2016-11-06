package com.chscodecamp.android.firetodo;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import hugo.weaving.DebugLog;

@DebugLog
public class ListActivity extends AppCompatActivity implements TaskRecyclerAdapter.Callback, TaskListStateListener, View.OnClickListener {

    private RecyclerView recyclerView;
    private EditText addItemEditText;
    private ImageButton addItem;
    private TaskRecyclerAdapter taskRecyclerAdapter;
    private List<Task> tasks;

    /**
     * Our TaskRecyclerAdapter.Callback Interface method onTaskUpdated() is called when a Task is un/marked
     * as completed.  This allows us to tell the TaskManager to update the Task from our Activity and
     * keeps the Adapter from having to know anything about the implementation details.
     *
     * @param task the item to be updated.
     */
    @Override
    public void onTaskUpdated(@NonNull Task task) {
        TaskManager.getInstance().updateTask(task);
    }

    /**
     * Our TaskListStateListener Interface method onTaskListUpdated() is called when the task list is
     * loaded from the database and allows us to notify the adapter of the change without the adapter
     * having to know about any of the implementation details.
     */
    @Override
    public void onTaskListUpdated() {
        if (taskRecyclerAdapter != null) {
            taskRecyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * By having our Activity implement View.OnClickListener we can keep the class' onCreate() method
     * a little cleaner.
     *
     * @param view the clicked View
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_item:

                /**
                 * Get the entered task and trim() it to remove any leading or trailing whitespace.
                 */
                String title = addItemEditText.getText().toString().trim();

                /**
                 * Do not allow the user to create duplicate tasks.  We do this by creating a new
                 * task from the entered text and then checking our list to see if the item can be
                 * found.  The indexOf() method will return -1 if the item does not exist in the list.
                 *
                 * The Task's hashCode() and equals() methods are what enable this kind of comparison.
                 */
                if (tasks.indexOf(new Task(title)) >= 0) {
                    /**
                     * EditText objects have a setError() method that allows you to give the users
                     * hints about what they've done wrong and need to fix.  They automatically
                     * disappear when the user starts entering text.
                     *
                     * We've also started moving Strings into the app/res/values/strings.xml file as
                     * this is considered a best practice (as opposed to hard coding the Strings here)
                     * and will simplify localization in the future.
                     */
                    addItemEditText.setError(decorateError(getString(R.string.error_msg_items_must_be_unique), 0xFFFFFFFF));

                } else if (!TextUtils.isEmpty(title)) {
                    /**
                     * The task is unique and was not null or empty so add it to the list and tell
                     * our adapter about the change so it can refresh the view.
                     */
                    Task task = new Task(title);
                    TaskManager.getInstance().addTask(task);
                    taskRecyclerAdapter.notifyItemInserted(taskRecyclerAdapter.getItemCount() - 1);
                    addItemEditText.setText(null);

                } else {
                    /**
                     * Do not allow the user to save an empty or whitespace-only task.
                     */
                    addItemEditText.setError(decorateError(getString(R.string.error_msg_todo_must_not_be_empty), 0xFFFFFFFF));
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        addItem = (ImageButton) findViewById(R.id.add_item);
        addItem.setOnClickListener(this);

        addItemEditText = (EditText) findViewById(R.id.add_item_edit_text);
        addItemEditText.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    addItem.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TaskManager.getInstance().setTaskListStateListener(this);
        tasks = TaskManager.getInstance().getTasks();
        if (taskRecyclerAdapter == null) {
            taskRecyclerAdapter = new TaskRecyclerAdapter(tasks, this);
            recyclerView.setAdapter(taskRecyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            taskRecyclerAdapter.notifyDataSetChanged();
        }
    }

    private CharSequence decorateError(String errorMessage, int color) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorMessage);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorMessage.length(), 0);
        return spannableStringBuilder;
    }
}
