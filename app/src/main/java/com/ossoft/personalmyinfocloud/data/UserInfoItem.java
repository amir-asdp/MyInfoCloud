package com.ossoft.personalmyinfocloud.data;

import java.util.ArrayList;

public class UserInfoItem {

    private String  mUID,mFirstName, mLastName, mEmail, mPhotoUrl, mIsFirstSignUp, mActiveSimNumber;
    private ArrayList<String> mAllSimNumbers, mLastLocation;

    public UserInfoItem(){}

    public UserInfoItem(String mUID, String mFirstName, String mLastName, String mEmail, String mPhotoUrl, ArrayList<String> mLastLocation, String mIsFirstSignUp, String mActiveSimNumber, ArrayList<String> mAllSimNumbers) {
        this.mUID = mUID;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mEmail = mEmail;
        this.mPhotoUrl = mPhotoUrl;
        this.mLastLocation = mLastLocation;
        this.mIsFirstSignUp = mIsFirstSignUp;
        this.mActiveSimNumber = mActiveSimNumber;
        this.mAllSimNumbers = mAllSimNumbers;
    }

    public String getmFirstName() {
        return mFirstName;
    }

    public String getmLastName() {
        return mLastName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public String getmPhotoUrl() {
        return mPhotoUrl;
    }

    public String getmUID() {
        return mUID;
    }

    public String getmActiveSimNumber() {
        return mActiveSimNumber;
    }

    public ArrayList<String> getmLastLocation() {
        return mLastLocation;
    }

    public String getmIsFirstSignUp() {
        return mIsFirstSignUp;
    }

    public ArrayList<String> getmAllSimNumbers() {
        return mAllSimNumbers;
    }
}
