package com.ossoft.personalmyinfocloud;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public abstract class MyCallReceiver extends BroadcastReceiver {

    private static int mLastState = TelephonyManager.CALL_STATE_IDLE;
    private static boolean isIncoming = false;

    public MyCallReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
        int state = TelephonyManager.CALL_STATE_IDLE;
        if(stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)){
            state = TelephonyManager.CALL_STATE_IDLE;
        }
        else if(stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
            state = TelephonyManager.CALL_STATE_OFFHOOK;
        }
        else if(stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)){
            state = TelephonyManager.CALL_STATE_RINGING;
        }


        String callType = callTypeDetector(state);
        onReceiveCall(callType);

    }



    private String callTypeDetector(int currentState) {
        String callType = "";

        if(mLastState == currentState){
            //No change, debounce extras
            return callType;
        }

        switch (currentState) {

            case TelephonyManager.CALL_STATE_RINGING:
                isIncoming = true;
                break;

            case TelephonyManager.CALL_STATE_OFFHOOK:
                if(mLastState != TelephonyManager.CALL_STATE_RINGING){
                    isIncoming = false;
                }
                else {
                    isIncoming = true;
                }

                break;

            case TelephonyManager.CALL_STATE_IDLE:
                if(mLastState == TelephonyManager.CALL_STATE_RINGING){
                    callType = "missed";
                }
                else if(isIncoming){
                    callType = "incoming_ended";
                }
                else if (mLastState == TelephonyManager.CALL_STATE_OFFHOOK){
                    callType = "outgoing_ended";
                }
                break;

        }
        mLastState = currentState;

        return callType;
    }

    protected abstract void onReceiveCall(String newCallType);


}
