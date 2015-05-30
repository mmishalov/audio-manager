package com.easysoft.audiomanager.shared;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;
import com.easysoft.audiomanager.activity.profile.ProfileSharedData;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Executes all activities from las selected profile
 * <p/>
 * <br/><i>Created at 11/24/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.4.x, JDK 1.7
 */
public class ActiveProfileActivitiesExecutor {
    private SharedPreferences sharedPreferences;
    private Context context;
    public ActiveProfileActivitiesExecutor(Context context){
        this.context = context;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public boolean execute(){
        String profilesJson = sharedPreferences.getString("profiles", "");
        ProfileSharedData sharedData=null;
        if(profilesJson!=null && !profilesJson.isEmpty()) {
            Gson gson = new Gson();
            HashMap<String, String> profiles = gson.fromJson(profilesJson,  HashMap.class);
            String sharedDataJson = profiles.get(sharedPreferences.getString("lastSelectedPosition", ""));
            if(sharedDataJson!=null &&  !sharedDataJson.isEmpty())
                sharedData = gson.fromJson(sharedDataJson,ProfileSharedData.class);
        }
        if(sharedData!=null) {
            PackageManager manager = context.getPackageManager();
            ArrayList<Intent> intents = new ArrayList<>();
            for (String packageName : sharedData.getSelectedApplications()) {
                Intent intent = manager.getLaunchIntentForPackage(packageName);
                if (intent != null) {
                    intent.addCategory(Intent.CATEGORY_LAUNCHER);
                    intents.add(intent);
                }
            }
            if (!intents.isEmpty())
                for (Intent intent : intents) {
                    Log.d(Constants.LOGGER_CONTEXT, "Starting " + intent.getPackage());
                    context.startActivity(intent);
                    Log.d(Constants.LOGGER_CONTEXT, "Intent " + intent.getPackage() + " started");
                }
            return sharedData.isSpeakersOn();
        }
        return false;
    }
}
