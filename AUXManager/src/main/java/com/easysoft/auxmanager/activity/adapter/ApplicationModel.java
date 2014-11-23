package com.easysoft.auxmanager.activity.adapter;


import android.content.pm.ApplicationInfo;

public class ApplicationModel {
    ApplicationInfo applicationInfo;
    boolean selected;

    public ApplicationModel(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
        selected = false;
    }

    public ApplicationModel(ApplicationInfo applicationInfo, boolean selected) {
        this.applicationInfo = applicationInfo;
        this.selected = selected;
    }

    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    public void setApplicationInfo(ApplicationInfo applicationInfo) {
        this.applicationInfo = applicationInfo;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
