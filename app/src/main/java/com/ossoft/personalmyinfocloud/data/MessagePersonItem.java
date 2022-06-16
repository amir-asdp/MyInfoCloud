package com.ossoft.personalmyinfocloud.data;

import java.util.ArrayList;
import java.util.List;

public class MessagePersonItem {

    private String mMessageNumber, mContactName, mContactEmail, mContactPhoto, mCountry, mReceiverPhoneNum;
    private ArrayList<String> mAllContactNumbers;

    public MessagePersonItem(){}

    public MessagePersonItem(String mMessageNumber, String mContactName, String mContactEmail, String mContactPhoto, String mCountry, String mReceiverPhoneNum, ArrayList<String> mAllContactNumbers) {
        this.mMessageNumber = mMessageNumber;
        this.mContactName = mContactName;
        this.mContactEmail = mContactEmail;
        this.mContactPhoto = mContactPhoto;
        this.mCountry = mCountry;
        this.mReceiverPhoneNum = mReceiverPhoneNum;
        this.mAllContactNumbers = mAllContactNumbers;
    }

    public String getmMessageNumber() {
        return mMessageNumber;
    }

    public String getmContactName() {
        return mContactName;
    }

    public String getmContactEmail() {
        return mContactEmail;
    }

    public String getmContactPhoto() {
        return mContactPhoto;
    }

    public String getmCountry() {
        return mCountry;
    }

    public String getmReceiverPhoneNum() {
        return mReceiverPhoneNum;
    }

    public List<String> getmAllContactNumbers() {
        return mAllContactNumbers;
    }
}
