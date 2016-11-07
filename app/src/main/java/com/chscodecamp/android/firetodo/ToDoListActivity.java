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

/**
 * The screen that will show our Task list and allow for task entry.
 */
@DebugLog
public class ToDoListActivity extends AppCompatActivity implements TaskRecyclerAdapter.Callback, TaskListStateListener, View.OnClickListener {

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
                    //noinspection deprecation // getColor(int) is deprecated, but is the only method available to us for API 16+
                    addItemEditText.setError(decorateError(getString(R.string.error_msg_items_must_be_unique), getResources().getColor(android.R.color.white)));

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
                    //noinspection deprecation // getColor(int) is deprecated, but is the only method available to us for API 16+
                    addItemEditText.setError(decorateError(getString(R.string.error_msg_todo_must_not_be_empty), getResources().getColor(android.R.color.white)));
                }
                break;
        }
    }

    /**
     * Executed 1 time when the activity is created.  Setup your views, click listeners, etc. here.
     *
     * @param savedInstanceState the incoming bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        /**
         * The + button that will add our task after a task is entered.
         */
        addItem = (ImageButton) findViewById(R.id.add_item);
        addItem.setOnClickListener(this);

        /**
         * We can also let the user press the "done" button on the keyboard to indicate the task
         * is ready to be added to the list.
         */
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

    /**
     * Unlike onCreate() which is only called when the activity is created, onResume() is called
     * any time activity comes into the foreground.  This is where we want to assign the data to
     * our adapter as the data may have changed while the application was in the background.
     */
    @Override
    protected void onResume() {
        super.onResume();

        /**
         * Set our activity as a listener so we can be notified when the StateManager updates
         * the data.
         */
        TaskManager.getInstance().setTaskListStateListener(this);

        /**
         * Get our tasks.  Note: we don't care how we get our tasks.  We simply count on the
         * getTasks() method to honor the contract of returning a List<Task>.
         */
        tasks = TaskManager.getInstance().getTasks();

        /**
         * If our adapter is null then set it up and assign the adapter to our view.
         */
        if (taskRecyclerAdapter == null) {
            taskRecyclerAdapter = new TaskRecyclerAdapter(tasks, this);
            recyclerView.setAdapter(taskRecyclerAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {

            /**
             * If we already had an adapter then just make sure we refresh the view.
             */
            taskRecyclerAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Our error message ends up being black-on-black for the given theme so lets decorate the
     * text such that it will be visible.
     *
     * @param errorMessage the text to be decorated
     * @param color        the color to apply to the text foreground
     * @return our text colored per the input color argument
     */
    private CharSequence decorateError(String errorMessage, int color) {
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(errorMessage);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, errorMessage.length(), 0);
        return spannableStringBuilder;
    }
}
