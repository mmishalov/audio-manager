package com.easysoft.auxmanager.activity;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;
import com.easysoft.auxmanager.R;
import com.easysoft.auxmanager.activity.adapter.ApplicationAdapter;
import com.easysoft.auxmanager.activity.adapter.ApplicationModel;

import java.util.ArrayList;
import java.util.List;

/**
 * The new profile activity
 * <p/>
 * <br/><i>Created at 7/5/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */

public class CreateNewProfileActivity  extends ListActivity {
    private CheckBox speakersOn;
    private List<ApplicationModel> appList = null;
    private ApplicationAdapter listAdaptor = null;
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_profile_activity);
        speakersOn = (CheckBox)findViewById(R.id.checkbox_speakers_on);
        Bundle receiveBundle = this.getIntent().getExtras();
        setTitle(getTitle() +" "+ receiveBundle.getString("profileName"));
        new LoadApplications().execute();
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


    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            appList = checkForLaunchIntent(getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA));
            listAdaptor = new ApplicationAdapter(CreateNewProfileActivity.this,  R.layout.snippet_list_row, appList);
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
            progress = ProgressDialog.show(CreateNewProfileActivity.this, null, "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
