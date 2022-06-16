package com.ossoft.personalmyinfocloud;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import es.dmoral.toasty.Toasty;

import static com.ossoft.personalmyinfocloud.LoginActivity.mGoogleSignInClient;
import static com.ossoft.personalmyinfocloud.MainActivity.mBaseCallList;
import static com.ossoft.personalmyinfocloud.MainActivity.mBaseMessageList;
import static com.ossoft.personalmyinfocloud.MainActivity.mBaseNoteList;
import static com.ossoft.personalmyinfocloud.MainActivity.mCallAdapter;
import static com.ossoft.personalmyinfocloud.MainActivity.mCurrentUser;
import static com.ossoft.personalmyinfocloud.MainActivity.mMessageAdapter;
import static com.ossoft.personalmyinfocloud.MainActivity.mNoteAdapter;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserDatabaseRef;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserInfoItem;
import static com.ossoft.personalmyinfocloud.StartupActivity.mIsLogin;

public class DeleteAccountDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private Activity mActivity;
    private TextInputEditText mPasswordEditTxt;
    private MaterialButton mCancelBtn, mConfirmBtn;
    private CustomProgressDialog mLoadingDialog;

    DeleteAccountDialog(@NonNull Context context, Activity activity) {
        super(context);
        mContext = context;
        mActivity = activity;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_delete_account);
        mPasswordEditTxt = findViewById(R.id.dialog_delete_account_password);
        mCancelBtn = findViewById(R.id.dialog_delete_account_cancel);
        mConfirmBtn = findViewById(R.id.dialog_delete_account_confirm);

        mLoadingDialog = new CustomProgressDialog(mContext, "Deleting your account ...");
        mLoadingDialog.setCanceledOnTouchOutside(false);
        mLoadingDialog.setCancelable(false);

        mCancelBtn.setOnClickListener(this);
        mConfirmBtn.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.dialog_delete_account_cancel:
                cancel();
                break;

            case R.id.dialog_delete_account_confirm:

                if (!mPasswordEditTxt.getText().toString().contains(" ") && !mPasswordEditTxt.getText().toString().equals("")){

                    mLoadingDialog.show();
                    AuthCredential credential = EmailAuthProvider.getCredential(mUserInfoItem.getmEmail(), mPasswordEditTxt.getText().toString().trim());
                    mCurrentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){

                                mUserDatabaseRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mCurrentUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()){
                                                        mGoogleSignInClient = GoogleSignIn.getClient(mContext, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(mContext.getString(R.string.default_web_client_id)).requestEmail().build());
                                                        if (mGoogleSignInClient != null){
                                                            mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        cancel();
                                                                        mLoadingDialog.cancel();
                                                                        mIsLogin.edit().putString("is_login", "false").apply();
                                                                        mBaseCallList.clear();
                                                                        mBaseMessageList.clear();
                                                                        mBaseNoteList.clear();
                                                                        mCallAdapter.notifyDataSetChanged();
                                                                        mMessageAdapter.notifyDataSetChanged();
                                                                        mNoteAdapter.notifyDataSetChanged();
                                                                        mContext.stopService(new Intent(mContext.getApplicationContext(), MyInfoListenerService.class));
                                                                        mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                                                        mActivity.finishAffinity();
                                                                        Toasty.success(mContext, "Your account deleted successfully").show();
                                                                    }else {
                                                                        Toasty.error(mContext, task.getException().getMessage()).show();
                                                                    }
                                                                }
                                                            });
                                                        }else {
                                                            cancel();
                                                            mLoadingDialog.cancel();
                                                            mIsLogin.edit().putString("is_login", "false").apply();
                                                            mBaseCallList.clear();
                                                            mBaseMessageList.clear();
                                                            mBaseNoteList.clear();
                                                            mCallAdapter.notifyDataSetChanged();
                                                            mMessageAdapter.notifyDataSetChanged();
                                                            mNoteAdapter.notifyDataSetChanged();
                                                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                                            mActivity.finishAffinity();
                                                            Toasty.success(mContext, "Your account deleted successfully").show();
                                                        }
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
