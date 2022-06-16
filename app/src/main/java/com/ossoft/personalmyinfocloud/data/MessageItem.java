package com.ossoft.personalmyinfocloud.data;

import java.io.Serializable;

public class MessageItem implements Serializable {

    private String mType;
    private String mDate;
    private String mText;
    private String mSentFrom;
    private long mMessageId;
    private MessagePersonItem mMessagePerson;

    public MessageItem(){}

    public MessageItem(String mType, String mDate, String mText, String mSentFrom, long mMessageId, MessagePersonItem mMessagePerson) {
        this.mType = mType;
        this.mDate = mDate;
        this.mText = mText;
        this.mSentFrom = mSentFrom;
        this.mMessageId = mMessageId;
        this.mMessagePerson = mMessagePerson;
    }

    public String getmType() {
        return mType;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmText() {
        return mText;
    }

    public String getmSentFrom() {
        return mSentFrom;
    }

    public long getmMessageId() {
        return mMessageId;
    }

    public MessagePersonItem getmMessagePerson() {
        return mMessagePerson;
    }
}
