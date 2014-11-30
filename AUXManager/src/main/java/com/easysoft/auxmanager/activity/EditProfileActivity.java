package com.easysoft.auxmanager.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.CheckBox;
import com.easysoft.auxmanager.R;
import com.easysoft.auxmanager.activity.adapter.ApplicationAdapter;
import com.easysoft.auxmanager.activity.adapter.ApplicationModel;
import com.easysoft.auxmanager.activity.profile.ProfileSharedData;
import com.easysoft.auxmanager.shared.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The new profile activity
 * <p/>
 * <br/><i>Created at 7/5/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */

public class EditProfileActivity extends ListActivity {
    private CheckBox speakersOn;
    private String profileName;
    private List<ApplicationModel> appList = null;
    private ApplicationAdapter listAdaptor = null;
    private SharedPreferences sharedPreferences;
    private ProfileSharedData sharedData;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile_activity);
        speakersOn = (CheckBox)findViewById(R.id.checkbox_speakers_on);
        Bundle receiveBundle = this.getIntent().getExtras();
        profileName = receiveBundle.getString("profileName");
        setTitle(getTitle() +" "+ profileName);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        String profilesJson = sharedPreferences.getString("profiles", "");
        if(profilesJson!=null && !profilesJson.isEmpty()) {
            Gson gson = new Gson();
            HashMap<String, String> profiles = gson.fromJson(profilesJson,  HashMap.class);
            String sharedDataJson = profiles.get(profileName);
            if(sharedDataJson!=null &&  !sharedDataJson.isEmpty())
                sharedData = gson.fromJson(sharedDataJson,ProfileSharedData.class);
        }

        new LoadApplications().execute();
        if(sharedData!=null){
            speakersOn.setChecked(sharedData.isSpeakersOn());
        }
    }

    private List<ApplicationModel> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationModel> appList = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {
                if (null != getPackageManager().getLaunchIntentForPackage(info.packageName)) {
                    appList.add(new ApplicationModel(info));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return appList;
    }
    private ProfileSharedData buildSharedData(){
        ProfileSharedData sharedData = new ProfileSharedData();
        sharedData.setProfileName(profileName);
        sharedData.setSpeakersOn(speakersOn.isChecked());
        for(ApplicationModel applicationModel: appList){
            if(applicationModel.isSelected()){
                sharedData.addSelectedApplication(applicationModel.getApplicationInfo().packageName);
            }
        }
        return sharedData;
    }
    @Override
    protected void onPause() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        String profilesJson = sharedPreferences.getString("profiles", "");
        HashMap<String,String> profiles;
        Gson gson = new Gson();
        if(profilesJson==null || profilesJson.isEmpty()) {
            profiles = new HashMap<>();
        }else {
            profiles = gson.fromJson(profilesJson, HashMap.class );
        }
        ProfileSharedData sharedData = buildSharedData();
        profiles.put(sharedData.getProfileName(),gson.toJson(sharedData));
        String json = gson.toJson(profiles);
        Log.d(Constants.LOGGER_CONTEXT, "commiting " + json);
        prefsEditor.putString("profiles", json);
        prefsEditor.apply();
        super.onPause();
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            appList = checkForLaunchIntent(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));
            if(sharedData!=null) {
                for (ApplicationModel applicationModel : appList) {
                    if(sharedData.getSelectedApplications().contains(applicationModel.getApplicationInfo().packageName)){
                        applicationModel.setSelected(true);
                    }
                }
            }
            listAdaptor = new ApplicationAdapter(EditProfileActivity.this,  R.layout.snippet_list_row, appList);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            setListAdapter(listAdaptor);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(EditProfileActivity.this, null, "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }
}
