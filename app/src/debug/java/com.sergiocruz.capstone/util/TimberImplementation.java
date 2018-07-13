package com.sergiocruz.capstone.util;
import timber.log.Timber;

public class TimberImplementation {

    private static DebugTree debugTree;

    public static void init() {
        // Avoid duplication
        if (debugTree == null) {
            debugTree = new DebugTree();
        } else {
            Timber.uprootAll();
        }
        Timber.plant(debugTree);
    }
}