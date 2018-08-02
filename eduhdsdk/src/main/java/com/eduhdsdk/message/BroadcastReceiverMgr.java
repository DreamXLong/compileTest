package com.eduhdsdk.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.talkcloud.roomsdk.TKRoomManager;

/**
 * Created by Administrator on 2017/9/13.
 */

public class BroadcastReceiverMgr extends BroadcastReceiver {

    private final String TAG = "classroom";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(TAG, "[Broadcast]" + action);

        //呼入电话
        if (action.equals(TelephonyManager.ACTION_PHONE_STATE_CHANGED)) {
            Log.i(TAG, "[Broadcast]PHONE_STATE");
            doReceivePhone(context, intent);
        }
    }

    /**
     * 处理电话广播.
     *
     * @param context
     * @param intent
     */
    public void doReceivePhone(Context context, Intent intent) {
        String phoneNumber = intent.getStringExtra(
                TelephonyManager.EXTRA_INCOMING_NUMBER);
        TelephonyManager telephony =
                (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int state = telephony.getCallState();
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.i(TAG, "[Broadcast]等待接电话=" + phoneNumber);
                TKRoomManager.getInstance().enableSendMyVoice(false);
                TKRoomManager.getInstance().enableOtherAudio(true);
                TKRoomManager.getInstance().setMuteAllStream(true);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.i(TAG, "[Broadcast]电话挂断=" + phoneNumber);
                if (TKRoomManager.getInstance().getMySelf() != null ) {
                    if (TKRoomManager.getInstance().getMySelf().publishState == 1 || TKRoomManager.getInstance().getMySelf().publishState == 3) {
                        TKRoomManager.getInstance().enableSendMyVoice(true);
                    }
                }
                TKRoomManager.getInstance().enableOtherAudio(false);
                TKRoomManager.getInstance().setMuteAllStream(false);
                TKRoomManager.getInstance().useLoudSpeaker(true);
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.i(TAG, "[Broadcast]通话中=" + phoneNumber);
                TKRoomManager.getInstance().enableSendMyVoice(false);
                TKRoomManager.getInstance().enableOtherAudio(true);
                TKRoomManager.getInstance().setMuteAllStream(true);
                break;
        }
    }
}