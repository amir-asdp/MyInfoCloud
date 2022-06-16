package com.ossoft.personalmyinfocloud.data;

import java.util.ArrayList;

public class ContactItem {

    private String mName, mPhotoUrl;
    private ArrayList<String> mAllContactEmails, mAllContactNumbers;

    public ContactItem(){}

    public ContactItem(String mName, ArrayList<String> mAllContactEmails, String mPhotoUrl, ArrayList<String> mAllContactNumbers) {
        this.mName = mName;
        this.mAllContactEmails = mAllContactEmails;
        this.mPhotoUrl = mPhotoUrl;
        this.mAllContactNumbers = mAllContactNumbers;
    }

    public String getmName() {
        return mName;
    }

    public ArrayList<String> getmAllContactEmails() {
        return mAllContactEmails;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public ArrayList<String> getmAllContactNumbers() {
        return mAllContactNumbers;
    }
}
