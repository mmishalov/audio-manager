package com.easysoft.audiomanager.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.easysoft.audiomanager.R;
import com.easysoft.audiomanager.activity.MainActivity;
import com.easysoft.audiomanager.listener.CallStateListener;
import com.easysoft.audiomanager.receiver.HeadsetActionBroadcastReceiver;
import com.easysoft.audiomanager.receiver.NotificationActionBroadcastReceiver;
import com.easysoft.audiomanager.shared.ActiveProfileActivitiesExecutor;
import com.easysoft.audiomanager.shared.Constants;
import com.easysoft.audiomanager.widget.AudioManagerWidgetProvider;

/**
 * Service implementation
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class AudioManagerService extends Service {
    private HeadsetActionBroadcastReceiver headsetActionReceiver;
    private NotificationActionBroadcastReceiver notificationActionReceiver;
    private CallStateListener callStateListener;
    private static boolean IS_ACTIVE = false;
    private int originalMode;
    private boolean originalSpeakerphoneOn;
    private TelephonyManager telephonyManager;
    private AudioManager audioManager;
    private static boolean IS_CHANGE_AUDIO = true;



    @Override
    public void onCreate() {
        super.onCreate();
        headsetActionReceiver = new HeadsetActionBroadcastReceiver();
        notificationActionReceiver = new NotificationActionBroadcastReceiver();
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        callStateListener = new CallStateListener(audioManager,  telephonyManager.getCallState());
        Log.d(Constants.LOGGER_CONTEXT, "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //IS_CHANGE_AUDIO = intent.getBooleanExtra("speakersOn",true);
        ActiveProfileActivitiesExecutor executor = new ActiveProfileActivitiesExecutor(getApplicationContext());
        IS_CHANGE_AUDIO = executor.execute();
        this.originalMode = audioManager.getMode();
        this.originalSpeakerphoneOn = audioManager.isSpeakerphoneOn();
        registerReceiver(headsetActionReceiver,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        registerReceiver(notificationActionReceiver, new IntentFilter(NotificationActionBroadcastReceiver.STOP_ACTION));
        registerReceiver(notificationActionReceiver, new IntentFilter(NotificationActionBroadcastReceiver.RESTART_ACTION));
        telephonyManager.listen(callStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        IS_ACTIVE = true;
        PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent stopPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(NotificationActionBroadcastReceiver.STOP_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent restartPendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(NotificationActionBroadcastReceiver.RESTART_ACTION), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notify_runing)
                .setTicker(getString(R.string.service_started))
                .setContentIntent(contentPendingIntent)
                .addAction(R.drawable.ic_action_remove, getString(R.string.action_stop), stopPendingIntent)
                .addAction(R.drawable.ic_action_refresh, getString(R.string.action_restart), restartPendingIntent)
                .setContentTitle(getString(R.string.service_name))
                .setContentInfo(getString(R.string.service_description))
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX).build();
        startForeground(1, notification);
        updateWidget();
        Log.d(Constants.LOGGER_CONTEXT, "Service started");

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWidget() {
        Intent widgetIntent = new Intent(this,AudioManagerWidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID, since it seems the onUpdate() is only fired on that
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), AudioManagerWidgetProvider.class));
        widgetIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(widgetIntent);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(headsetActionReceiver);
        unregisterReceiver(notificationActionReceiver);
        telephonyManager.listen(callStateListener,PhoneStateListener.LISTEN_NONE);
        audioManager.setMode(this.originalMode);
        audioManager.setSpeakerphoneOn(originalSpeakerphoneOn);
        IS_ACTIVE = false;
        updateWidget();
        stopForeground(true);
        Log.d(Constants.LOGGER_CONTEXT, "Service destroyed");
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public static boolean isServiceActive(){
        return IS_ACTIVE;
    }
    public static boolean isChangeAudio(){
        return IS_CHANGE_AUDIO;
    }
}
