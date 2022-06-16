package com.ossoft.personalmyinfocloud.data;

import java.util.ArrayList;
import java.util.List;

public class CallPersonItem {

    private String mCallNumber, mContactName, mContactEmail, mContactPhoto, mCountry;
    private ArrayList<String> mAllContactNumbers;

    public CallPersonItem(){}

    public CallPersonItem(String mCallNumber, String mContactName, String mContactEmail, String mContactPhoto, String mCountry, ArrayList<String> mAllContactNumbers) {
        this.mCallNumber = mCallNumber;
        this.mContactName = mContactName;
        this.mContactEmail = mContactEmail;
        this.mContactPhoto = mContactPhoto;
        this.mCountry = mCountry;
        this.mAllContactNumbers = mAllContactNumbers;
    }

    public String getmCallNumber() {
        return mCallNumber;
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

    public List<String> getmAllContactNumbers() {
        return mAllContactNumbers;
    }
}
