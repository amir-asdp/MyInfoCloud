package com.ossoft.personalmyinfocloud;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

import static com.ossoft.personalmyinfocloud.MainActivity.mBaseCallList;
import static com.ossoft.personalmyinfocloud.MainActivity.mBaseMessageList;
import static com.ossoft.personalmyinfocloud.MainActivity.mBaseNoteList;
import static com.ossoft.personalmyinfocloud.MainActivity.mCallAdapter;
import static com.ossoft.personalmyinfocloud.MainActivity.mCurrentUser;
import static com.ossoft.personalmyinfocloud.MainActivity.mMessageAdapter;
import static com.ossoft.personalmyinfocloud.MainActivity.mNoteAdapter;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserAuth;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserDatabaseRef;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserInfoItem;
import static com.ossoft.personalmyinfocloud.StartupActivity.mIsLogin;

public class ChangeEmailDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private HashMap<String, Object> mNewInfoMap;

    private TextInputEditText mPasswordEditTxt;
    private MaterialButton mCancelBtn, mConfirmBtn;
    private CustomProgressDialog mLoadingDialog;

    ChangeEmailDialog(@NonNull Context context, HashMap<String, Object> newInfoMap) {
        super(context);
        mContext = context;
        mNewInfoMap = newInfoMap;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_change_email);
        mPasswordEditTxt = findViewById(R.id.dialog_change_email_password);
        mCancelBtn = findViewById(R.id.dialog_change_email_cancel);
        mConfirmBtn = findViewById(R.id.dialog_change_email_confirm);

        mLoadingDialog = new CustomProgressDialog(mContext, "Saving Changes ...");
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);

        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.dialog_change_email_cancel:
                cancel();
                break;

            case R.id.dialog_change_email_confirm:

                if (!mPasswordEditTxt.getText().toString().contains(" ") && !mPasswordEditTxt.getText().toString().equals("")){

                    mLoadingDialog.show();
                    AuthCredential credential = EmailAuthProvider.getCredential(mUserInfoItem.getmEmail(), mPasswordEditTxt.getText().toString().trim());
                    mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                mCurrentUser.updateEmail(((String) mNewInfoMap.get("mEmail"))).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){

                                            mUserDatabaseRef.child("mUserInfo").updateChildren(mNewInfoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){

                                                        FirebaseAuth.getInstance().getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){

                                                                    mUserAuth.signOut();
                                                                    cancel();
                                                                    mLoadingDialog.cancel();
                                                                    Toasty.success(mContext, "Your information updated successfully").show();

                                                                    new MaterialAlertDialogBuilder(mContext)
                                                                            .setIcon(R.drawable.ic_verification)
                                                                            .setTitle("Email Verification")
                                                                            .setMessage("Verification Email was sent to new Email. Please verify your new Email and then login")
                                                                            .setPositiveButton("OK", new OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    mBaseCallList.clear();
                                                                                    mBaseMessageList.clear();
                                                                                    mBaseNoteList.clear();
                                                                                    mCallAdapter.notifyDataSetChanged();
                                                                                    mMessageAdapter.notifyDataSetChanged();
                                                                                    mNoteAdapter.notifyDataSetChanged();
                                                                                    mIsLogin.edit().putString("is_login", "false").apply();
                                                                                    mContext.stopService(new Intent(mContext.getApplicationContext(), MyInfoListenerService.class));
                                                                                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                                                                    ((ProfileActivity) mContext).finishAffinity();
                                                                                }
                                                                            }).setCancelable(false).show();

                                                                }else {
                                                                    cancel();
                                                                    mLoadingDialog.cancel();
                                                                    Toasty.error(mContext, task.getException().getMessage()).show();
                                                                }
                                                            }
                                                        });

                                                    }else {
                                                        cancel();
                                                        mLoadingDialog.cancel();
                                                        Toasty.error(mContext, task.getException().getMessage()).show();
                                                    }
                                                }
                                            });

                                        }else {
                                            cancel();
                                            mLoadingDialog.cancel();
                                            Toasty.error(mContext, task.getException().getMessage()).show();
                                        }
                                    }
                                });

                            }else {
                                cancel();
                                mLoadingDialog.cancel();
                                Toasty.error(mContext, task.getException().getMessage()).show();
                            }
                        }
                    });


                }else {
                    Toasty.error(mContext, "Password is not valid").show();
                }

                break;

        }

    }


}
