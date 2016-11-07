package com.chscodecamp.android.firetodo;

/**
 * Our Task Object.  This will hold all the things we wish to track with regards to a Task.
 */
@SuppressWarnings("WeakerAccess")
class Task {
    public String title;
    public boolean completed;

    public Task() {
    }

    Task(String title) {
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    @SuppressWarnings("unused")
    public void setTitle(String title) {
        this.title = title;
    }

    boolean getCompleted() {
        return completed;
    }

    void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;

        Task task = (Task) o;

        if (getCompleted() != task.getCompleted()) return false;
        return getTitle().equals(task.getTitle());

    }

    @Override
    public int hashCode() {
        int result = getTitle().hashCode();
        result = 31 * result + (getCompleted() ? 1 : 0);
        return result;
    }
}
