package com.ossoft.personalmyinfocloud;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserDatabaseRef;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.ossoft.personalmyinfocloud.data.NoteItem;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class AddNewNoteDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private MaterialButton mSaveNote, mCancelDialog;
    private TextView mNoteSubject, mNoteText;
    private CustomProgressDialog mSavingDialog;



    public AddNewNoteDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_add_new_note);
        mCancelDialog = findViewById(R.id.new_note_dialog_cancel);
        mSaveNote = findViewById(R.id.new_note_dialog_save);
        mNoteSubject = findViewById(R.id.new_note_dialog_subject);
        mNoteText = findViewById(R.id.new_note_dialog_text);

        mCancelDialog.setOnClickListener(this);
        mSaveNote.setOnClickListener(this);

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.new_note_dialog_cancel:
                cancel();
                break;

            case R.id.new_note_dialog_save:
                if (mNoteSubject.getText().toString().trim().length() == 0 || mNoteText.getText().toString().trim().length() == 0){
                    Toasty.error(mContext, "Please complete al of the fields").show();
                }else {
                    mSavingDialog = new CustomProgressDialog(mContext, "Saving ...");
                    mSavingDialog.setCanceledOnTouchOutside(false);
                    mSavingDialog.setCancelable(false);
                    mSavingDialog.show();

                    long currentMillisecond = System.currentTimeMillis();
                    NoteItem newNote = new NoteItem(mNoteText.getText().toString().trim(), mNoteSubject.getText().toString().trim(), dateFormatter(currentMillisecond), currentMillisecond*-1);
                    HashMap<String, Object> noteMap = new HashMap<>();
                    noteMap.put(String.valueOf(currentMillisecond), newNote);
                    mUserDatabaseRef.child("mNoteList").updateChildren(noteMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                cancel();
                                mSavingDialog.cancel();
                                Toasty.success(mContext, "The note added successfully").show();
                            }else {
                                mSavingDialog.cancel();
                                Toasty.error(mContext, task.getException().getMessage()).show();
                            }
                        }
                    });
                }
                break;

        }

    }



    public String dateFormatter(long timeMillis){
        return DateFormat.format("dd/MM/yyyy\nHH:mm", timeMillis).toString();
    }


}
