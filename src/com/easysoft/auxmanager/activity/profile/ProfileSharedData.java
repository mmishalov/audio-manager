package com.easysoft.auxmanager.activity.profile;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents SharedData for Profiles
 * <p/>
 * <br/><i>Created at 7/6/2014 10:57 PM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class ProfileSharedData {
    ////////////////////////////////////////////////////[VARIABLES]////////////////////////////////////////////////////
    private String profileName;
    private boolean speakersOn;
    private Set<String> selectedApplications = new HashSet<>();
    /////////////////////////////////////////////////////[METHODS]/////////////////////////////////////////////////////
    public boolean isSpeakersOn() {
        return speakersOn;
    }

    public void setSpeakersOn(boolean speakersOn) {
        this.speakersOn = speakersOn;
    }

    public Set<String> getSelectedApplications() {
        return selectedApplications;
    }

    public void setSelectedApplications(Set<String> selectedApplications) {
        this.selectedApplications = selectedApplications;
    }
    public void addSelectedApplication(String packageName){
        selectedApplications.add(packageName);
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }
}