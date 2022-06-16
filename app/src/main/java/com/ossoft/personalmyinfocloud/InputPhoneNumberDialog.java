package com.ossoft.personalmyinfocloud;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.ossoft.personalmyinfocloud.data.UserInfoItem;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

import static com.ossoft.personalmyinfocloud.MainActivity.mAllUserPhoneNumbers;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserDatabaseRef;

public abstract class InputPhoneNumberDialog extends Dialog {

    private Context mContext;
    private String mAction;
    private UserInfoItem mUserInfoItem;
    private ArrayList<String> mNewPhoneNumberList = new ArrayList<>();

    private TextInputEditText mNumberEditTxt;
    private ChipGroup mNumberChipGroup;
    private MaterialButton mAddNumberBtn, mSaveBtn;

    private String ACTION_INITIALIZE = "initialize", ACTION_EDIT = "edit";

    InputPhoneNumberDialog(@NonNull Context context){
        super(context);
        mContext = context;
        mAction = ACTION_INITIALIZE;
    }

    InputPhoneNumberDialog(@NonNull Context context, UserInfoItem userInfoItem) {
        super(context);
        mContext = context;
        mAction = ACTION_EDIT;
        mUserInfoItem = userInfoItem;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_input_phone_number);
        mNumberEditTxt = findViewById(R.id.dialog_input_phone_number_edit_txt);
        mNumberChipGroup = findViewById(R.id.dialog_input_phone_number_chip_grp);
        mAddNumberBtn = findViewById(R.id.dialog_input_phone_number_add);
        mSaveBtn = findViewById(R.id.dialog_input_phone_number_save);



        if (mAction.equals(ACTION_EDIT)){
            mNewPhoneNumberList = mUserInfoItem.getmAllSimNumbers();
            for (String number : mNewPhoneNumberList){
                final Chip chip = new Chip(mContext);
                chip.setTag(number);
                chip.setText("SIM Slot " + (mNewPhoneNumberList.indexOf(number)+1) + ": " + number);
                chip.setTextColor(mContext.getResources().getColor(R.color.white));
                chip.setChipBackgroundColorResource(R.color.colorPrimary);
                chip.setCloseIconResource(R.drawable.ic_delete_account_white);
                chip.setCloseIconTintResource(R.color.white);
                chip.setCloseIconVisible(true);
                chip.setCloseIconStartPadding(4);

                chip.setOnCloseIconClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mNewPhoneNumberList.remove(chip.getTag().toString());
                        mNumberChipGroup.removeView(v);
                        for (String tag : mNewPhoneNumberList){
                            ((Chip) mNumberChipGroup.findViewWithTag(tag)).setText("SIM Slot " + (mNewPhoneNumberList.indexOf(tag)+1) + ": " + tag);
                        }
                    }
                });

                mNumberChipGroup.addView(chip);
            }
        }



        mAddNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = mNumberEditTxt.getText().toString().trim();
                if (phoneNumber.isEmpty() || phoneNumber.contains(" ") || !Patterns.PHONE.matcher(phoneNumber).matches() || phoneNumber.length()<9 || phoneNumber.length()>12){
                    Toasty.error(mContext, "Invalid number").show();
                }
                else if (mNewPhoneNumberList.contains(phoneNumber)){
                    Toasty.error(mContext, "The number is already exist").show();
                }
                else if (mNewPhoneNumberList.size() == 4){
                    Toasty.error(mContext, "You can not add more than 4 numbers").show();
                }
                else {
                    mNewPhoneNumberList.add(phoneNumber);
                    final Chip chip = new Chip(mContext);
                    chip.setTag(phoneNumber);
                    chip.setText("SIM Slot " + (mNewPhoneNumberList.indexOf(phoneNumber)+1) + ": " + phoneNumber);
                    chip.setTextColor(mContext.getResources().getColor(R.color.white));
                    chip.setChipBackgroundColorResource(R.color.colorPrimary);
                    chip.setCloseIconResource(R.drawable.ic_delete_account_white);
                    chip.setCloseIconTintResource(R.color.white);
                    chip.setCloseIconVisible(true);
                    chip.setCloseIconStartPadding(4);

                    chip.setOnCloseIconClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mNewPhoneNumberList.remove(chip.getTag().toString());
                            mNumberChipGroup.removeView(v);
                            for (String tag : mNewPhoneNumberList){
                                ((Chip) mNumberChipGroup.findViewWithTag(tag)).setText("SIM Slot " + (mNewPhoneNumberList.indexOf(tag)+1) + ": " + tag);
                            }
                        }
                    });

                    mNumberChipGroup.addView(chip);
                    mNumberEditTxt.setText("");
                }
            }
        });



        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNewPhoneNumberList.size() > 0){
                    if (mAction.equals(ACTION_EDIT)){
                        onSaveChanges(mNewPhoneNumberList);
                        cancel();
                    }
                    else {
                        mAllUserPhoneNumbers = mNewPhoneNumberList;
                        mUserDatabaseRef.child("mUserInfo").child("mAllSimNumbers").setValue(mAllUserPhoneNumbers).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    onSaveChanges(mNewPhoneNumberList);
                                    cancel();
                                }else {
                                    Toasty.error(mContext, task.getException().getMessage()).show();
                                }
                            }
                        });
                    }
                }
                else {
                    Toasty.error(mContext, "There must be at least one Phone number").show();
                }
            }
        });


    }



    protected abstract void onSaveChanges(ArrayList<String> newNumberList);


}
