package com.ossoft.personalmyinfocloud.data;

public class NoteItem {

    private String mText;
    private String mSubject;
    private String mDate;
    private long mNoteId;

    public NoteItem(){}

    public NoteItem(String mText, String mSubject, String mDate, long mNoteId) {
        this.mText = mText;
        this.mSubject = mSubject;
        this.mDate = mDate;
        this.mNoteId = mNoteId;
    }

    public String getmText() {
        return mText;
    }

    public String getmDate() {
        return mDate;
    }

    public String getmSubject() {
        return mSubject;
    }

    public long getmNoteId() {
        return mNoteId;
    }

}
