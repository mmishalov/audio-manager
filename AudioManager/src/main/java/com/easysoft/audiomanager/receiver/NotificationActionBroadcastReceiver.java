package com.easysoft.audiomanager.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.easysoft.audiomanager.service.AudioManagerService;
import com.easysoft.audiomanager.shared.Constants;


/**
 * Manages actions received from notification shadow
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class NotificationActionBroadcastReceiver extends BroadcastReceiver {

    public static String STOP_ACTION        = "com.easysoft.audiomanager.action.ACTION_STOP";
    public static String RESTART_ACTION     = "com.easysoft.audiomanager.action.ACTION_RESTART";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(Constants.LOGGER_CONTEXT, "Notification action received:" + action);
        Intent audioManagerIntent  = new Intent(context, AudioManagerService.class);
        if(STOP_ACTION.equals(action) || RESTART_ACTION.equals(action) ){
            context.stopService(audioManagerIntent);
            if(RESTART_ACTION.equals(action)){
                context.startService(audioManagerIntent);
            }
        }
    }

}