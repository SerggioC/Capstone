package com.sergiocruz.capstone.util;

public class TimberImplementation {
    public static void init() {
        Timber.plant(new ReleaseTree());
        return;
    }
}