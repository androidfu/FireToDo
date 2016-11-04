package com.chscodecamp.android.firetodo;

import java.util.List;

interface DataSetChangedListener {
    void onDataSetChanged(List<Task> taskList);
}
