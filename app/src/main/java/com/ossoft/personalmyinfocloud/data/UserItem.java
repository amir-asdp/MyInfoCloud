package com.ossoft.personalmyinfocloud.data;

import java.util.ArrayList;

public class UserItem {

    private ArrayList<CallItem> mCallList;
    private ArrayList<MessageItem> mMessageList;
    private ArrayList<NoteItem> mNoteList;
    private ArrayList<ContactItem> mContactList;
    private UserInfoItem mUserInfo;

    public UserItem(){}

    public UserItem(ArrayList<CallItem> mCallList, ArrayList<MessageItem> mMessageList, ArrayList<NoteItem> mNoteList, ArrayList<ContactItem> mContactList, UserInfoItem mUserInfo) {
        this.mCallList = mCallList;
        this.mMessageList = mMessageList;
        this.mNoteList = mNoteList;
        this.mContactList = mContactList;
        this.mUserInfo = mUserInfo;
    }

    public ArrayList<CallItem> getmCallList() {
        return mCallList;
    }

    public ArrayList<MessageItem> getmMessageList() {
        return mMessageList;
    }

    public ArrayList<NoteItem> getmNoteList() {
        return mNoteList;
    }

    public ArrayList<ContactItem> getmContactList() {
        return mContactList;
    }

    public UserInfoItem getmUserInfo() {
        return mUserInfo;
    }
}
