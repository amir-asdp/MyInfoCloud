package com.ossoft.personalmyinfocloud.data;

import java.io.Serializable;

public class CallItem implements Serializable {

    private String mType;
    private String mCallDuration;
    private String mDate;
    private long mCallId;
    private CallPersonItem mCallPerson;

    public CallItem(){}

    public CallItem(String mType, String mCallDuration, String mDate, long mCallId, CallPersonItem mCallPerson) {
        this.mType = mType;
        this.mCallDuration = mCallDuration;
        this.mDate = mDate;
        this.mCallId = mCallId;
        this.mCallPerson = mCallPerson;
    }

    public String getmType() {
        return mType;
    }

    public String getmCallDuration() {
        return mCallDuration;
    }

    public String getmDate() {
        return mDate;
    }

    public long getmCallId() {
        return mCallId;
    }

    public CallPersonItem getmCallPerson() {
        return mCallPerson;
    }
}
