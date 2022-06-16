package com.ossoft.personalmyinfocloud;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.provider.Telephony;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.ossoft.personalmyinfocloud.data.CallItem;
import com.ossoft.personalmyinfocloud.data.CallPersonItem;
import com.ossoft.personalmyinfocloud.data.ContactItem;
import com.ossoft.personalmyinfocloud.data.MessageItem;
import com.ossoft.personalmyinfocloud.data.MessagePersonItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;

import static com.ossoft.personalmyinfocloud.MainActivity.mAllUserPhoneNumbers;

public class MyInfoListenerService extends Service {

    private String CHANNEL_ID = "channelId";
    private NotificationManager mNotificationManager;
    private MySmsObserver mMySmsObserver;
    private static BroadcastReceiver mMyCallReceiver;
    FirebaseUser mCurrentUser;
    DatabaseReference mUserDatabaseRef;
    ValueEventListener mContactEventListener;
    PhoneNumberOfflineGeocoder mGeocoder;
    PhoneNumberUtil mPhoneUtil;
    InfoServiceCallBack mInfoServiceCallBack;
    ArrayList<ContactItem> mContactList = new ArrayList<>();



    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMyCallReceiver);
        MyInfoListenerService.this.getContentResolver().unregisterContentObserver(mMySmsObserver);
        Log.e("-----------", "destroyed");
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mGeocoder = PhoneNumberOfflineGeocoder.getInstance();
        mPhoneUtil = PhoneNumberUtil.getInstance();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String offerChannelName = "Service Channel";
            String offerChannelDescription= "State Receiver Channel";
            int offerChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, offerChannelName, offerChannelImportance);
            channel.setDescription(offerChannelDescription);
            mNotificationManager = getSystemService(NotificationManager.class);
            mNotificationManager.createNotificationChannel(channel);

        }


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("MyInfo Cloud")
                .setContentText("Realtime state listener enabled.");


        Notification serviceNotification = notificationBuilder.build();


        startForeground(1, serviceNotification);


        mMyCallReceiver = new MyCallReceiver() {
            @Override
            protected void onReceiveCall(String newCallType) {
                mInfoServiceCallBack.onNewCall(newCallType);
            }
        };
        mMySmsObserver = new MySmsObserver(new Handler(), MyInfoListenerService.this) {
            @Override
            protected void onReceiveMessage(String newMessageType) {
                mInfoServiceCallBack.onNewSms(newMessageType);
            }
        };
        registerMyInfoListeners();


        mInfoServiceCallBack = new InfoServiceCallBack() {
            @Override
            public void onNewCall(final String newCallType) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED && checkInternetConnection(getApplicationContext()) && mCurrentUser != null){
                        mContactEventListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mContactList.clear();
                                if (snapshot.getValue() != null){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        mContactList.add(dataSnapshot.getValue(ContactItem.class));
                                    }

                                    @SuppressLint("MissingPermission") Cursor callLogCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " DESC");

                                    if (callLogCursor != null && callLogCursor.moveToFirst()){
                                        if (!newCallType.equals("")) {
                                            long callId = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE))*-1;
                                            String callNumber = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));
                                            String callDuration = String.valueOf(callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DURATION)));
                                            String callDate = dateFormatter(callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE)));
                                            String callType = "";
                                            CallPersonItem callPerson = null;
                                            try {
                                                callPerson = new CallPersonItem(callNumber, "", "", "", mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(callNumber, "GR"), Locale.getDefault()), null);
                                            } catch (NumberParseException e) {
                                                e.printStackTrace();
                                            }

                                            switch (callLogCursor.getInt(callLogCursor.getColumnIndex(CallLog.Calls.TYPE))){
                                                case CallLog.Calls.INCOMING_TYPE:
                                                    if (newCallType.equals("incoming_ended")){
                                                        callType = "incoming";
                                                    }else {
                                                        callType = "type_match_error";
                                                    }
                                                    break;
                                                case CallLog.Calls.OUTGOING_TYPE:
                                                    if (newCallType.equals("outgoing_ended")){
                                                        callType = "outgoing";
                                                    }else {
                                                        callType = "type_match_error";
                                                    }
                                                    break;
                                                case CallLog.Calls.MISSED_TYPE:
                                                case CallLog.Calls.REJECTED_TYPE:
                                                    if (newCallType.equals("missed")){
                                                        callType = "missed";
                                                    }else {
                                                        callType = "type_match_error";
                                                    }
                                                    break;
                                            }

                                            for (ContactItem contactItem : mContactList){
                                                if (contactItem.getmAllContactNumbers() != null){
                                                    if (contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(callNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)) || contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(callNumber, PhoneNumberUtil.PhoneNumberFormat.E164))){
                                                        try {
                                                            callPerson = new CallPersonItem(phoneNumberFormatter(callNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL), contactItem.getmName(), (contactItem.getmAllContactEmails() != null)?contactItem.getmAllContactEmails().get(0):"", contactItem.getmPhotoUrl(), mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(callNumber, "GR"), Locale.getDefault()), contactItem.getmAllContactNumbers());
                                                        } catch (NumberParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                }
                                            }

                                            CallItem newCallItem = new CallItem(callType, callDuration, callDate, callId, callPerson);
                                            HashMap<String, Object> newCallMap = new HashMap<>();
                                            newCallMap.put(String.valueOf(callId), newCallItem);
                                            mUserDatabaseRef.child("mCallList").updateChildren(newCallMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toasty.success(MyInfoListenerService.this, "MyInfo Cloud: New call added successfully", Toasty.LENGTH_LONG).show();
                                                    }else {
                                                        Toasty.error(MyInfoListenerService.this, task.getException().getMessage()).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(getApplicationContext(), error.getMessage()).show();
                            }
                        };
                        mUserDatabaseRef.child("mContactList").addListenerForSingleValueEvent(mContactEventListener);
                    }
                }
            }

            @Override
            public void onNewSms(String newMessageType) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED && checkInternetConnection(getApplicationContext()) && mCurrentUser != null){
                        mContactEventListener = new ValueEventListener() {
                            @SuppressLint("MissingPermission")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mContactList.clear();
                                if (snapshot.getValue() != null){
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        mContactList.add(dataSnapshot.getValue(ContactItem.class));
                                    }

                                    Cursor messageCursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, Telephony.Sms.DATE + " DESC");
                                    SubscriptionManager subscriptionManager = ((SubscriptionManager) getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE));
                                    mAllUserPhoneNumbers.clear();
                                    for (SubscriptionInfo subscriptionInfo : subscriptionManager.getActiveSubscriptionInfoList()){
                                        mAllUserPhoneNumbers.add(subscriptionInfo.getNumber());
                                    }

                                    if (messageCursor != null && messageCursor.moveToFirst()){
                                        EasyDeviceMod easyDeviceMod = new EasyDeviceMod(MyInfoListenerService.this);
                                        int receiverSimId;
                                        if (easyDeviceMod.getManufacturer().contains("xiaomi") || easyDeviceMod.getManufacturer().contains("Xiaomi")){
                                            receiverSimId = Integer.parseInt(String.valueOf(messageCursor.getLong(messageCursor.getColumnIndex("sim_id"))));
                                        }else {
                                            receiverSimId = Integer.parseInt(String.valueOf(messageCursor.getLong(messageCursor.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID))));
                                        }
                                        long messageId = messageCursor.getLong(messageCursor.getColumnIndex(Telephony.Sms.DATE))*-1;
                                        String senderNumberOrDisplayName = messageCursor.getString(messageCursor.getColumnIndex(Telephony.Sms.ADDRESS));
                                        String messageDate = dateFormatter(messageCursor.getLong(messageCursor.getColumnIndex(Telephony.Sms.DATE)));
                                        String messageText = messageCursor.getString(messageCursor.getColumnIndex(Telephony.Sms.BODY));
                                        String sentFrom = "phone";
                                        String messageType = "";
                                        MessagePersonItem messagePerson = null;
                                        try {
                                            messagePerson = new MessagePersonItem(senderNumberOrDisplayName, "", "", "", (senderNumberOrDisplayName!=null && Patterns.PHONE.matcher(senderNumberOrDisplayName).matches())?mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(senderNumberOrDisplayName, "GR"), Locale.getDefault()):"", (receiverSimId != 0)?mAllUserPhoneNumbers.get(receiverSimId-1):"", null);
                                        } catch (NumberParseException e) {
                                            e.printStackTrace();
                                        }

                                        switch (messageCursor.getInt(messageCursor.getColumnIndex(Telephony.Sms.TYPE))){
                                            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                                                messageType = "incoming";
                                                break;
                                            case Telephony.Sms.MESSAGE_TYPE_SENT:
                                                messageType = "outgoing";
                                                break;
                                        }

                                        if (senderNumberOrDisplayName != null && Patterns.PHONE.matcher(senderNumberOrDisplayName).matches()){
                                            for (ContactItem contactItem : mContactList){
                                                if (contactItem.getmAllContactNumbers() != null){
                                                    if (contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(senderNumberOrDisplayName, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)) || contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(senderNumberOrDisplayName, PhoneNumberUtil.PhoneNumberFormat.E164))){
                                                        try {
                                                            messagePerson = new MessagePersonItem(phoneNumberFormatter(senderNumberOrDisplayName, PhoneNumberUtil.PhoneNumberFormat.NATIONAL), contactItem.getmName(), (contactItem.getmAllContactEmails() != null)?contactItem.getmAllContactEmails().get(0):"", contactItem.getmPhotoUrl(), mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(senderNumberOrDisplayName, "GR"), Locale.getDefault()), (receiverSimId != 0)?mAllUserPhoneNumbers.get(receiverSimId-1):"", contactItem.getmAllContactNumbers());
                                                        } catch (NumberParseException e) {
                                                            e.printStackTrace();
                                                        }
                                                        break;
                                                    }
                                                }
                                            }
                                        }

                                        if (senderNumberOrDisplayName != null){
                                            MessageItem newMessageItem = new MessageItem(messageType, messageDate, messageText, sentFrom, messageId, messagePerson);
                                            HashMap<String, Object> newMessageMap = new HashMap<>();
                                            newMessageMap.put(String.valueOf(messageId), newMessageItem);
                                            mUserDatabaseRef.child("mMessageList").updateChildren(newMessageMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        Toasty.success(MyInfoListenerService.this, "MyInfo Cloud: New message added successfully", Toasty.LENGTH_LONG).show();
                                                    }else {
                                                        Toasty.error(MyInfoListenerService.this, task.getException().getMessage()).show();
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toasty.error(getApplicationContext(), error.getMessage()).show();
                            }
                        };
                        mUserDatabaseRef.child("mContactList").addListenerForSingleValueEvent(mContactEventListener);
                    }
                }
            }
        };


        return START_STICKY;
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public void registerMyInfoListeners(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(mMyCallReceiver, intentFilter, "android.permission.BROADCAST_SMS", null);
        MyInfoListenerService.this.getContentResolver().registerContentObserver(Telephony.Sms.CONTENT_URI, true, mMySmsObserver);
    }



    public boolean checkInternetConnection(Context context){
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            Toasty.warning(context, "MyInfo Cloud: No internet connection").show();
            return false;
        }
        return true;
    }



    public String dateFormatter(long timeMillis){
        return DateFormat.format("dd/MM/yyyy\nHH:mm", timeMillis).toString();
    }



    public String phoneNumberFormatter(String phoneNumber, PhoneNumberUtil.PhoneNumberFormat requiredFormat){

        switch (requiredFormat){
            case INTERNATIONAL:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
            case NATIONAL:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.NATIONAL).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
            case E164:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.E164).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
            case RFC3966:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.RFC3966).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        return "";
    }



    public interface InfoServiceCallBack{
        void onNewCall(String newCallType);
        void onNewSms(String newMessageType);
    }


}
