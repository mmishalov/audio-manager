package com.easysoft.auxmanager.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.easysoft.auxmanager.R;
import com.easysoft.auxmanager.service.AUXManagerService;
import com.easysoft.auxmanager.shared.Constants;
/**
 * Main activity
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    Switch manageAUXSettingsSwitch;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        manageAUXSettingsSwitch = (Switch)findViewById(R.id.manageAUXSettingsSwitch);
        manageAUXSettingsSwitch.setOnCheckedChangeListener(this);
        manageAUXSettingsSwitch.setChecked(AUXManagerService.isServiceActive());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent auxManagerIntent  = new Intent(this, AUXManagerService.class);
        if(isChecked){
            startService(auxManagerIntent);
        }else {
            stopService(auxManagerIntent);
        }
        Log.d(Constants.CONTEXT,"Service is "  + (isChecked ? "on" : "off"));
    }

    @Override
    protected void onDestroy() {
        if(!AUXManagerService.isServiceActive()){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onDestroy();
    }
}

