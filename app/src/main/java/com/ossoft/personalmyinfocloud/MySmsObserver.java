package com.ossoft.personalmyinfocloud;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;
import android.util.Log;

public abstract class MySmsObserver extends ContentObserver {

    private Context mContext;
    private static long mLastIncomingMessageID;
    private static long mLastOutgoingMessageID;

    MySmsObserver(Handler handler, Context context) {
        super(handler);
        mContext = context;
        Cursor incomingMessageCursor = mContext.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
        Cursor outgoingMessageCursor = mContext.getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
        if (incomingMessageCursor.moveToFirst()){
            mLastIncomingMessageID = incomingMessageCursor.getLong(incomingMessageCursor.getColumnIndex(Telephony.Sms.DATE));
        }
        if (outgoingMessageCursor.moveToFirst()){
            mLastOutgoingMessageID = outgoingMessageCursor.getLong(outgoingMessageCursor.getColumnIndex(Telephony.Sms.DATE));
        }
        incomingMessageCursor.close();
        outgoingMessageCursor.close();
    }



    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        Cursor incomingMessageCursor = mContext.getContentResolver().query(Telephony.Sms.Inbox.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
        Cursor outgoingMessageCursor = mContext.getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");

        if (incomingMessageCursor.moveToFirst()){
            long currentIncomingMessageID = incomingMessageCursor.getLong(incomingMessageCursor.getColumnIndex(Telephony.Sms.DATE));
            if (mLastIncomingMessageID != currentIncomingMessageID){
                switch (incomingMessageCursor.getInt(incomingMessageCursor.getColumnIndex(Telephony.Sms.TYPE))){
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        onReceiveMessage("incoming");
                        Log.e("----------", "in");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        onReceiveMessage("outgoing");
                        Log.e("----------", "in*");
                        break;
                }
            }

            mLastIncomingMessageID = currentIncomingMessageID;
        }

        if (outgoingMessageCursor.moveToFirst()){
            long currentOutgoingMessageID = outgoingMessageCursor.getLong(outgoingMessageCursor.getColumnIndex(Telephony.Sms.DATE));
            if (mLastOutgoingMessageID != currentOutgoingMessageID){
                switch (outgoingMessageCursor.getInt(outgoingMessageCursor.getColumnIndex(Telephony.Sms.TYPE))){
                    case Telephony.Sms.MESSAGE_TYPE_INBOX:
                        onReceiveMessage("incoming");
                        Log.e("----------", "out*");
                        break;
                    case Telephony.Sms.MESSAGE_TYPE_SENT:
                        onReceiveMessage("outgoing");
                        Log.e("----------", "out");
                        break;
                }
            }

            mLastOutgoingMessageID = currentOutgoingMessageID;
        }
    }



    protected abstract void onReceiveMessage(String newMessageType);

}
