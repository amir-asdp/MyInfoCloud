package com.ossoft.personalmyinfocloud;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

import static com.ossoft.personalmyinfocloud.MainActivity.mUserDatabaseRef;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class ItemInfoDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String mAction;
    private String mCallPhotoUrl, mCallName, mCallLocation, mCallDuration, mCallType, mCallDate, mCallId, mCallerEmail, mCallerNumber, mCallerOtherNumbers;
    private String mMessagePhotoUrl, mMessageName, mMessageLocation, mSendFrom, mMessageType, mMessageDate, mMessageId, mMessageEmail, mMessageReceiverNumber, mMessageSenderNumber, mOtherSenderNumbers, mMessageText;
    private String mNoteSubject, mNoteText; private long mNoteId;
    private CustomProgressDialog mDeleteDialog;

    public ItemInfoDialog(@NonNull Context context, String mCallPhotoUrl, String mCallName, String mCallLocation, String mCallDuration, String mCallType, String mCallDate, String mCallId, String mCallerEmail, String mCallerNumber, String mCallerOtherNumbers) {
        super(context);
        this.mAction = "call";
        this.mContext = context;
        this.mCallPhotoUrl = mCallPhotoUrl;
        this.mCallName = mCallName;
        this.mCallLocation = mCallLocation;
        this.mCallDuration = mCallDuration;
        this.mCallType = mCallType;
        this.mCallDate = mCallDate;
        this.mCallId = mCallId;
        this.mCallerEmail = mCallerEmail;
        this.mCallerNumber = mCallerNumber;
        this.mCallerOtherNumbers = mCallerOtherNumbers;
    }

    public ItemInfoDialog(@NonNull Context context, String mMessagePhotoUrl, String mMessageName, String mMessageLocation, String mSendFrom, String mMessageType, String mMessageDate, String mMessageId, String mMessageEmail, String mMessageReceiverNumber, String mMessageSenderNumber, String mOtherSenderNumbers, String mMessageText) {
        super(context);
        this.mContext = context;
        this.mAction = "message";
        this.mMessagePhotoUrl = mMessagePhotoUrl;
        this.mMessageName = mMessageName;
        this.mMessageLocation = mMessageLocation;
        this.mSendFrom = mSendFrom;
        this.mMessageType = mMessageType;
        this.mMessageDate = mMessageDate;
        this.mMessageId = mMessageId;
        this.mMessageEmail = mMessageEmail;
        this.mMessageReceiverNumber = mMessageReceiverNumber;
        this.mMessageSenderNumber = mMessageSenderNumber;
        this.mOtherSenderNumbers = mOtherSenderNumbers;
        this.mMessageText = mMessageText;
    }

    public ItemInfoDialog(@NonNull Context context, String mNoteSubject, String mNoteText, long mNoteId) {
        super(context);
        this.mAction = "note";
        this.mContext = context;
        this.mNoteSubject = mNoteSubject;
        this.mNoteText = mNoteText;
        this.mNoteId = mNoteId;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        switch (mAction){

            case "call":
                setContentView(R.layout.dialog_call_item_info);
                TextView callName = findViewById(R.id.call_dialog_caller_name), callLocation = findViewById(R.id.call_dialog_caller_location), callDuration = findViewById(R.id.call_dialog_duration), callDate = findViewById(R.id.call_dialog_date), callId = findViewById(R.id.call_dialog_call_id), callEmail = findViewById(R.id.call_dialog_caller_email), callNumber =findViewById(R.id.call_dialog_caller_number), otherCallerNumbers = findViewById(R.id.call_dialog_other_numbers);
                ImageView callDialogCancel = findViewById(R.id.call_dialog_cancel), callType = findViewById(R.id.call_dialog_type_logo);
                CircleImageView callerPhoto = findViewById(R.id.call_dialog_caller_profile_pic);

                if (mCallPhotoUrl.equals("")){
                    callerPhoto.setImageResource(R.drawable.ic_account);
                }else {
                    Glide.with(mContext).load(Uri.parse(mCallPhotoUrl)).placeholder(R.drawable.ic_account).fitCenter().into(callerPhoto);
                }
                if (mCallName.equals("")){
                    callName.setText("Unknown");
                }else {
                    callName.setText(mCallName);
                }
                if (mCallLocation.equals("")){
                    callLocation.setText("Unknown");
                }else {
                    callLocation.setText(mCallLocation);
                }
                if (mCallerEmail.equals("")){
                    callEmail.setText("Not found");
                }else {
                    callEmail.setText(mCallerEmail);
                }
                if (mCallerOtherNumbers.equals("")){
                    otherCallerNumbers.setText("Not found");
                }else {
                    otherCallerNumbers.setText(mCallerOtherNumbers);
                }
                callDuration.setText(mCallDuration + " s");
                callDate.setText(mCallDate);
                callId.setText(mCallId);
                callNumber.setText(mCallerNumber);
                switch (mCallType){
                    case "incoming":
                        callType.setImageResource(R.drawable.ic_incoming);
                        break;
                    case "outgoing":
                        callType.setImageResource(R.drawable.ic_outgoing);
                        break;
                    case "missed":
                        callType.setImageResource(R.drawable.ic_missed);
                        break;
                }
                callDialogCancel.setOnClickListener(this);
                break;



            case "message":
                setContentView(R.layout.dialog_message_item_info);
                TextView messageName = findViewById(R.id.message_dialog_message_name), messageLocation = findViewById(R.id.message_dialog_message_location), messageSendFrom = findViewById(R.id.message_dialog_send_from), messageDate = findViewById(R.id.message_dialog_date), messageId = findViewById(R.id.message_dialog_message_id), messageEmail = findViewById(R.id.message_dialog_message_email), messageReceiverNumber = findViewById(R.id.message_dialog_receiver_number), messageSenderNumber = findViewById(R.id.message_dialog_message_number), messageSenderOtherNumbers = findViewById(R.id.message_dialog_other_numbers), messageText = findViewById(R.id.message_dialog_text);
                ImageView messageDialogCancel = findViewById(R.id.message_dialog_cancel), messageType = findViewById(R.id.message_dialog_type_logo);
                CircleImageView messagePhoto = findViewById(R.id.message_dialog_message_profile_pic);

                if (mMessagePhotoUrl.equals("")){
                    messagePhoto.setImageResource(R.drawable.ic_account);
                }else {
                    Glide.with(mContext).load(Uri.parse(mMessagePhotoUrl)).placeholder(R.drawable.ic_account).fitCenter().into(messagePhoto);
                }
                if (mMessageName.equals("")){
                    messageName.setText("Unknown");
                }else {
                    messageName.setText(mMessageName);
                }
                if (mMessageLocation.equals("")){
                    messageLocation.setText("Unknown");
                }else {
                    messageLocation.setText(mMessageLocation);
                }
                if (mMessageEmail.equals("")){
                    messageEmail.setText("Note found");
                }else {
                    messageEmail.setText(mMessageEmail);
                }
                if (mMessageReceiverNumber != null){
                    if (mMessageReceiverNumber.equals("")){
                        messageReceiverNumber.setText("Not found");
                    }else {
                        messageReceiverNumber.setText(mMessageReceiverNumber);
                    }
                }else {
                    messageReceiverNumber.setText("Not found");
                }
                if (mOtherSenderNumbers.equals("")){
                    messageSenderOtherNumbers.setText("Not found");
                }else {
                    messageSenderOtherNumbers.setText(mOtherSenderNumbers);
                }
                messageSendFrom.setText(mSendFrom);
                messageDate.setText(mMessageDate);
                messageId.setText(mMessageId);
                messageSenderNumber.setText(mMessageSenderNumber);
                messageText.setText(mMessageText);
                switch (mMessageType){
                    case "incoming":
                        messageType.setImageResource(R.drawable.ic_incoming);
                        break;
                    case "outgoing":
                        messageType.setImageResource(R.drawable.ic_outgoing);
                        break;
                }
                messageDialogCancel.setOnClickListener(this);
                break;



            case "note":
                setContentView(R.layout.dialog_note_item_info);
                TextView noteSubject = findViewById(R.id.note_dialog_subject), noteText = findViewById(R.id.note_dialog_text);
                ImageView noteDialogCancel = findViewById(R.id.note_dialog_cancel);
                MaterialButton deleteNote = findViewById(R.id.note_dialog_delete_note);
                noteSubject.setText(mNoteSubject);
                noteText.setText(mNoteText);
                noteDialogCancel.setOnClickListener(this);
                deleteNote.setOnClickListener(this);
                break;

        }

    }



    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.call_dialog_cancel){
            cancel();
        }else if (v.getId() == R.id.message_dialog_cancel){
            cancel();
        }else if (v.getId() == R.id.note_dialog_cancel){
            cancel();
        }else if (v.getId() == R.id.note_dialog_delete_note){

            mDeleteDialog = new CustomProgressDialog(mContext, "Deleting ...");
            mDeleteDialog.setCanceledOnTouchOutside(false);
            mDeleteDialog.setCancelable(false);
            mDeleteDialog.show();

            mUserDatabaseRef.child("mNoteList").child(String.valueOf(mNoteId*-1)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mDeleteDialog.cancel();
                        cancel();
                        Toasty.success(mContext, "The note deleted successfully").show();
                    }else {
                        mDeleteDialog.cancel();
                        cancel();
                        Toasty.error(mContext, task.getException().getMessage()).show();
                    }
                }
            });
        }

    }


}
