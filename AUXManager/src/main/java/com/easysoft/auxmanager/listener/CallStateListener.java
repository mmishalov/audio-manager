package com.easysoft.auxmanager.listener;


import android.media.AudioManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.easysoft.auxmanager.shared.Constants;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Manages speaker behavior depended on phone call state
 * <p/>
 * <br/><i>Created at 2/16/14 2:03 AM, user: mishalov</i>
 *
 * @author Michael Mishalov
 * @since Android SDK 4.1, JDK 1.7
 */
public class CallStateListener extends PhoneStateListener {
    private AudioManager audioManager;
    private int callState;

    public CallStateListener(AudioManager audioManager, int callState) {
        super();
        this.audioManager = audioManager;
        this.callState =  callState;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (callState) {
            case TelephonyManager.CALL_STATE_IDLE:
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    Log.d(Constants.CONTEXT, "Idle => Off Hook = new outgoing call");
                } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Log.d(Constants.CONTEXT, "Idle => Ringing = new incoming call (" + incomingNumber + ")");
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                    scheduler.schedule(new Runnable() {
                        @Override
                        public void run() {
                            audioManager.setMode(AudioManager.STREAM_MUSIC);
                            audioManager.setSpeakerphoneOn(true);
                            Log.d(Constants.CONTEXT, "Off Hook => Idle  = disconnected, speaker on");
                        }
                    },5, TimeUnit.SECONDS);

                } else if (state == TelephonyManager.CALL_STATE_RINGING) {
                    Log.d(Constants.CONTEXT, "Off Hook => Ringing = another call waiting (" + incomingNumber + ")");
                }
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    audioManager.setSpeakerphoneOn(false);
                    Log.d(Constants.CONTEXT, "Ringing => Off Hook = received, speaker off");
                } else if (state == TelephonyManager.CALL_STATE_IDLE) {
                    Log.d(Constants.CONTEXT, "Ringing => Idle = missed call");
                }
                break;
            default:
        }
        callState = state;
    }

}
