package com.sergiocruz.capstone.util;

import timber.log.Timber;

public class TimberImplementation {
    public static void init() {
        Timber.plant(new ReleaseTree());
        return;
    }
}