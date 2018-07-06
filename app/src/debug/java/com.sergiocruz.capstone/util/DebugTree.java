package com.sergiocruz.capstone.util;

import com.crashlytics.android.Crashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import timber.log.Timber;

public class DebugTree extends Timber.DebugTree {

    @Override
    protected @Nullable String createStackElementTag(@NotNull StackTraceElement element) {
        return String.format("Sergio> %s; Method %s; Line %s",
                super.createStackElementTag(element),
                element.getMethodName(),
                element.getLineNumber());
    }

    @Override
    protected void log(int priority, @Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        Crashlytics.log(priority, tag, message);
    }
}
