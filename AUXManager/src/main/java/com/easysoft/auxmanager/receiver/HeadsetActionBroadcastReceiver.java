package com.easysoft.auxmanager.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;
import com.easysoft.auxmanager.service.AUXManagerService;
import com.easysoft.auxmanager.shared.Constants;
/**
 * Manages speaker behavior depended on headset activity
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class HeadsetActionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
            int state = intent.getIntExtra("state", -1);
            if(AUXManagerService.isChangeAudio())
                switch (state) {
                    case 0:
                    case 1:
                        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                        audioManager.setMode(AudioManager.STREAM_MUSIC);
                        audioManager.setSpeakerphoneOn(true);
                        Log.d(Constants.LOGGER_CONTEXT, "Headset is " + ((state==0)? "unplugged" : "plugged"));
                        break;
                    default:
                }
        }
    }
}
