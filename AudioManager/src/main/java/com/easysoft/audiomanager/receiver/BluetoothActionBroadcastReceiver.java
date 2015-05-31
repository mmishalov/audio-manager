package com.easysoft.audiomanager.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import com.easysoft.audiomanager.shared.Constants;

/**
 * Manages speaker behavior depended on bluetooth activity
 * <p/>
 * <br/><i>Created at 5/31/2015 11:58 PM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class BluetoothActionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(BluetoothDevice.ACTION_ACL_CONNECTED.equals(intent.getAction())){
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            audioManager.setBluetoothScoOn(false);
            audioManager.stopBluetoothSco();
            audioManager.setMode(AudioManager.STREAM_MUSIC);
            audioManager.setSpeakerphoneOn(true);
            Log.d(Constants.LOGGER_CONTEXT, "Paired bluetooth device connected");
        }

    }
}
