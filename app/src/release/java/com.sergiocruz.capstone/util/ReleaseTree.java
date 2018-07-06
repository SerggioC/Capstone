package com.sergiocruz.capstone.util;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

import static android.util.Log.ERROR;
import static android.util.Log.WARN;

class ReleaseTree extends Timber.Tree {

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        if (priority == ERROR || priority == WARN)
            Crashlytics.log(priority, tag, message);
    }
}
