package com.easysoft.auxmanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;
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
    ToggleButton startServiceToggleButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        startServiceToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        startServiceToggleButton.setOnCheckedChangeListener(this);
        startServiceToggleButton.setChecked(AUXManagerService.isServiceActive());
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_actions, menu);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new_profile:
                createNewProfile();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void createNewProfile() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.new_profile_name);
        // Set up the input
        final EditText input = new EditText(this);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Bundle sendBundle = new Bundle();
                sendBundle.putString("profileName", input.getText().toString());
                Intent i = new Intent(getApplicationContext(), CreateNewProfileActivity.class);
                i.putExtras(sendBundle);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    protected void onDestroy() {
        if(!AUXManagerService.isServiceActive()){
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        super.onDestroy();
    }
}

