package com.chscodecamp.android.firetodo;

import android.app.Application;

import hugo.weaving.DebugLog;

@DebugLog
public class FireToDoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TaskManager.init(new FirebaseStateManager());
    }
}
