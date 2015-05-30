package com.easysoft.audiomanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.easysoft.audiomanager.R;
import com.easysoft.audiomanager.receiver.NotificationActionBroadcastReceiver;
import com.easysoft.audiomanager.service.AudioManagerService;
import com.easysoft.audiomanager.shared.Constants;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main activity
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener {
    private ToggleButton startServiceToggleButton;
    private Spinner spinner;
    private ArrayAdapter<String> spinnerArrayAdapter;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedPreferenceChangeListener;
    private BroadcastReceiver serviceStopActionReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        startServiceToggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        startServiceToggleButton.setOnCheckedChangeListener(this);
        startServiceToggleButton.setChecked(AudioManagerService.isServiceActive());
        spinner = (Spinner) findViewById(R.id.profiles_spinner);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        spinnerArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, new ArrayList<String>()); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);
        updateProfilesSpinner();
        initializeListeners();
        initializeReceivers();
    }
    @Override
    protected void onResume() {
        startServiceToggleButton.setChecked(AudioManagerService.isServiceActive());
        super.onResume();
    }
    private void initializeReceivers() {
        serviceStopActionReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(NotificationActionBroadcastReceiver.STOP_ACTION)){
                    startServiceToggleButton.setChecked(false);
                }

            }
        };
    }
    private void initializeListeners() {
        //Shared Preferences
        sharedPreferenceChangeListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                Log.d(Constants.LOGGER_CONTEXT, "Keu is " + key);
                updateProfilesSpinner();
            }
        };
        sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        // Buttons
        ImageButton button = (ImageButton) findViewById(R.id.edit_profile);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("profileName", spinner.getSelectedItem().toString());
                editProfile(bundle);
            }
        });
        button = (ImageButton) findViewById(R.id.delete_profile);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle(R.string.delete_profile_dialog_title)
                        .setMessage(R.string.delete_profile_dialog_message)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String profilesJson = sharedPreferences.getString("profiles", "");
                                if (profilesJson != null && !profilesJson.isEmpty()) {
                                    Gson gson = new Gson();
                                    HashMap<String, String> profiles = gson.fromJson(profilesJson, HashMap.class);
                                    profiles.remove(spinner.getSelectedItem().toString());
                                    SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
                                    prefsEditor.putString("profiles", gson.toJson(profiles));
                                    prefsEditor.apply();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
        AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                storeLastSelectedProfile();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        spinner.setOnItemSelectedListener(onItemSelectedListener);
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
        Intent audioManagerIntent  = new Intent(this, AudioManagerService.class);
        if(isChecked && !AudioManagerService.isServiceActive()){
            //ActiveProfileActivitiesExecutor activeProfileActivitiesExecutor = new ActiveProfileActivitiesExecutor(this.getApplicationContext());
            //audioManagerIntent.putExtra("speakersOn", activeProfileActivitiesExecutor.execute());
            startService(audioManagerIntent);
        }else if(!isChecked && AudioManagerService.isServiceActive()){
            stopService(audioManagerIntent);
        }
        Log.d(Constants.LOGGER_CONTEXT,"Service is "  + (isChecked ? "on" : "off"));
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
                Bundle bundle = new Bundle();
                bundle.putString("profileName", input.getText().toString());
                editProfile(bundle);
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
    private void editProfile(Bundle bundle){
        Intent i = new Intent(getApplicationContext(), EditProfileActivity.class);
        i.putExtras(bundle);
        startActivity(i);
    }
    private void updateProfilesSpinner(){
        String profilesJson = sharedPreferences.getString("profiles", "");
        if(profilesJson!=null && !profilesJson.isEmpty()) {
            Gson gson = new Gson();
            HashMap<String, String> profiles = gson.fromJson(profilesJson, HashMap.class);
            spinnerArrayAdapter.clear();
            spinnerArrayAdapter.addAll(profiles.keySet());
        }
        String lastSelection =  sharedPreferences.getString("lastSelectedPosition", "");
        if(lastSelection!=null && !lastSelection.isEmpty()){
            spinner.setSelection(spinnerArrayAdapter.getPosition(lastSelection));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver((serviceStopActionReceiver), new IntentFilter(NotificationActionBroadcastReceiver.STOP_ACTION));
    }

    @Override
    protected void onStop() {
        unregisterReceiver(serviceStopActionReceiver);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onDestroy() {
        storeLastSelectedProfile();
        if(!AudioManagerService.isServiceActive())
            android.os.Process.killProcess(android.os.Process.myPid());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
        super.onDestroy();
    }

    private void storeLastSelectedProfile() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        prefsEditor.putString("lastSelectedPosition",spinner.getSelectedItem().toString());
        prefsEditor.apply();
    }
}

