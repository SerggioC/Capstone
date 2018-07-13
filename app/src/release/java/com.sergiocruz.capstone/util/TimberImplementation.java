package com.sergiocruz.capstone.util;

import timber.log.Timber;

public class TimberImplementation {

    private static ReleaseTree releaseTree;

    public static void init() {
        if (releaseTree == null) {
            releaseTree = new ReleaseTree();
        } else {
            Timber.uprootAll();
        }
        Timber.plant(releaseTree);
    }

}