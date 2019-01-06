package com.github.openwebnet.model.firestore;

import android.os.Build;

public class ProfileSnapshot {

    private String name;

    private String androidManufacturer;

    private String androidModel;

    private int androidVersionSdk;

    private String androidVersionRelease;

    public ProfileSnapshot() {}

    public ProfileSnapshot(String name) {
        this.name = name;
        this.androidManufacturer = Build.MANUFACTURER;
        this.androidModel = Build.MODEL;
        this.androidVersionSdk = Build.VERSION.SDK_INT;
        this.androidVersionRelease = Build.VERSION.RELEASE;
    }

    public String getName() {
        return name;
    }

    public String getAndroidManufacturer() {
        return androidManufacturer;
    }

    public String getAndroidModel() {
        return androidModel;
    }

    public int getAndroidVersionSdk() {
        return androidVersionSdk;
    }

    public String getAndroidVersionRelease() {
        return androidVersionRelease;
    }

}
