package com.easysoft.auxmanager.listener;


import android.content.Context;
import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.easysoft.auxmanager.shared.Constants;
/**
 * Manages speaker behavior depended on phone call state
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class CallStateListener extends PhoneStateListener {
    Context context;
    public CallStateListener(Context context) {
        this.context = context;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                audioManager.setMode(AudioManager.STREAM_MUSIC);
                audioManager.setSpeakerphoneOn(true);
                Log.d(Constants.CONTEXT,"Phone is Idle, speaker on");
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                audioManager.setSpeakerphoneOn(false);
                Log.d(Constants.CONTEXT,"Phone is Off Hook, speaker off");
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d(Constants.CONTEXT,"Phone Ringing (" + incomingNumber + ") ");
                break;
            default:
        }
        super.onCallStateChanged(state,incomingNumber);
    }

}
