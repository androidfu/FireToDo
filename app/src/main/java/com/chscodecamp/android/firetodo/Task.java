package com.chscodecamp.android.firetodo;

class Task {
    public String title;
    @SuppressWarnings("WeakerAccess")
    public boolean completed;

    public Task() {
    }

    Task(String title) {
        this.title = title;
    }

    String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    boolean getCompleted() {
        return completed;
    }

    void setCompleted(boolean completed) {
        this.completed = completed;
    }

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
