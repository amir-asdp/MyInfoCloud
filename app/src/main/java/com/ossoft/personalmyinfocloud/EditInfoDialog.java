package com.ossoft.personalmyinfocloud;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.ossoft.personalmyinfocloud.data.UserInfoItem;

import java.util.ArrayList;
import java.util.HashMap;

import es.dmoral.toasty.Toasty;

import static com.ossoft.personalmyinfocloud.LoginActivity.RC_SIGN_IN;
import static com.ossoft.personalmyinfocloud.LoginActivity.mGoogleSignInClient;
import static com.ossoft.personalmyinfocloud.MainActivity.mAllUserPhoneNumbers;
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

public class EditInfoDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String mAccountProviderType;
    private UserInfoItem mCurrentUserInfo;
    private AuthCredential mPreviousCredential;
    private GoogleSignInClient mPreviousGoogleSignInClient;
    private GoogleSignInAccount mNewGoogleSignInAccount;
    private ArrayList<String> mNewPhoneNumberList;
    private boolean isClientChanged;

    private TextView mPhoneNumberTxv, mGoogleEmailTxv, mNoticeTxv;
    private EditText mFirstNameEditTxt, mLastNameEditText, mEmailEditTxt;
    private MaterialButton mSaveEditBtn, mCancelEditBtn, mEditPhoneNumberBtn, mChangeGoogleEmailBtn;
    private LinearLayout mGoogleEmailContainer;
    private CustomProgressDialog mSavingDialog;
    private CustomProgressDialog mLoadingDialog;

    EditInfoDialog(@NonNull Context context, UserInfoItem currentUserInfo) {
        super(context);
        mContext = context;
        mCurrentUserInfo = currentUserInfo;
        mAccountProviderType = accountProviderDetector();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_edit_info);

        mPhoneNumberTxv = findViewById(R.id.edit_info_dialog_phone_numbers);
        mGoogleEmailTxv = findViewById(R.id.edit_info_dialog_email_google);
        mNoticeTxv = findViewById(R.id.edit_info_dialog_notice);
        mFirstNameEditTxt = findViewById(R.id.edit_info_dialog_first_name);
        mLastNameEditText = findViewById(R.id.edit_info_dialog_last_name);
        mEmailEditTxt = findViewById(R.id.edit_info_dialog_email);
        mSaveEditBtn = findViewById(R.id.edit_info_dialog_save);
        mCancelEditBtn = findViewById(R.id.edit_info_dialog_cancel);
        mEditPhoneNumberBtn = findViewById(R.id.edit_info_dialog_edit_phone_number);
        mChangeGoogleEmailBtn = findViewById(R.id.edit_info_dialog_change_email_google);
        mGoogleEmailContainer = findViewById(R.id.edit_info_dialog_google_account_container);

        if (mUserInfoItem.getmAllSimNumbers() != null){
            String phoneNumbers = "";
            for (String number : mUserInfoItem.getmAllSimNumbers()){
                if (mUserInfoItem.getmAllSimNumbers().indexOf(number) == mUserInfoItem.getmAllSimNumbers().size()-1){
                    phoneNumbers = phoneNumbers.concat(number);
                }else {
                    phoneNumbers = phoneNumbers.concat(number).concat("\n");
                }
            }
            mPhoneNumberTxv.setText(phoneNumbers);
        }
        mFirstNameEditTxt.setText(mCurrentUserInfo.getmFirstName());
        mLastNameEditText.setText(mCurrentUserInfo.getmLastName());
        mSaveEditBtn.setOnClickListener(this);
        mCancelEditBtn.setOnClickListener(this);
        mEditPhoneNumberBtn.setOnClickListener(this);


        if (mAccountProviderType.equals("google.com")){
            mPreviousCredential = GoogleAuthProvider.getCredential(GoogleSignIn.getLastSignedInAccount(mContext).getIdToken(), null);
            mPreviousGoogleSignInClient = GoogleSignIn.getClient(mContext, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(mContext.getString(R.string.default_web_client_id)).requestEmail().build());
            mNoticeTxv.setText("NOTICE: If you want to change your Email, you have to login again after changing the Email");
            mGoogleEmailTxv.setText(mCurrentUser.getEmail());
            mChangeGoogleEmailBtn.setOnClickListener(this);
            mGoogleEmailContainer.setVisibility(View.VISIBLE);
            mEmailEditTxt.setVisibility(View.GONE);
        }else {
            mNoticeTxv.setText("NOTICE: If you want to change your Email, you have to verify the new Email. After verification you will be able to login to your account again. So if you enter a fake Email Address you won't be able to recover your data.");
            mEmailEditTxt.setText(mCurrentUser.getEmail());
            mEmailEditTxt.setVisibility(View.VISIBLE);
            mGoogleEmailContainer.setVisibility(View.GONE);
        }

        mSavingDialog = new CustomProgressDialog(mContext, "Saving Changes ...");
        mSavingDialog.setCanceledOnTouchOutside(false);
        mSavingDialog.setCancelable(false);


    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.edit_info_dialog_save:
                String firstName = mFirstNameEditTxt.getText().toString().trim();
                String lastName = mLastNameEditText.getText().toString().trim();
                if (mNewPhoneNumberList != null){
                    mAllUserPhoneNumbers = mNewPhoneNumberList;
                }

                if (mAccountProviderType.equals("google.com")){
                    final String email = mGoogleEmailTxv.getText().toString().trim();
                    final HashMap<String, Object> infoMap = new HashMap<>();
                    infoMap.put("mFirstName", firstName);
                    infoMap.put("mLastName", lastName);
                    infoMap.put("mAllSimNumbers", mAllUserPhoneNumbers);
                    infoMap.put("mEmail", email);

                    if (!isClientChanged){
                        mSavingDialog.show();
                        mUserDatabaseRef.child("mUserInfo").updateChildren(infoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    cancel();
                                    mSavingDialog.cancel();
                                    Toasty.success(mContext, "Your information updated successfully").show();
                                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                                    ((ProfileActivity) mContext).finishAffinity();
                                }else {
                                    mSavingDialog.cancel();
                                    Toasty.error(mContext, task.getException().getMessage()).show();
                                }
                            }
                        });
                    }
                    else {
                        mSavingDialog.show();
                        mCurrentUser.reauthenticate(mPreviousCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    mCurrentUser.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                if (mNewGoogleSignInAccount != null){
                                                    mGoogleSignInClient.signOut();
                                                    mGoogleSignInClient = GoogleSignIn.getClient(mContext, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(mContext.getString(R.string.default_web_client_id)).requestEmail().build());
                                                    FirebaseAuth.getInstance().signOut();
                                                    FirebaseAuth.getInstance().signInWithCredential(GoogleAuthProvider.getCredential(mNewGoogleSignInAccount.getIdToken(), null)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if (task.isSuccessful()){

                                                                mUserDatabaseRef.child("mUserInfo").updateChildren(infoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){

                                                                            cancel();
                                                                            mSavingDialog.cancel();
                                                                            mGoogleSignInClient.signOut();
                                                                            FirebaseAuth.getInstance().signOut();
                                                                            mBaseCallList.clear();
                                                                            mBaseMessageList.clear();
                                                                            mBaseNoteList.clear();
                                                                            mCallAdapter.notifyDataSetChanged();
                                                                            mMessageAdapter.notifyDataSetChanged();
                                                                            mNoteAdapter.notifyDataSetChanged();
                                                                            mIsLogin.edit().putString("is_login", "false").apply();
                                                                            Toasty.success(mContext, "Your information updated successfully").show();
                                                                            mContext.stopService(new Intent(mContext.getApplicationContext(), MyInfoListenerService.class));
                                                                            mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                                                            ((ProfileActivity) mContext).finishAffinity();

                                                                        }else {
                                                                            mSavingDialog.cancel();
                                                                            Toasty.error(mContext, task.getException().getMessage()).show();
                                                                        }
                                                                    }
                                                                });

                                                            }else {
                                                                mSavingDialog.cancel();
                                                                Toasty.error(mContext, task.getException().getMessage()).show();
                                                            }
                                                        }
                                                    });
                                                }else {
                                                    mUserDatabaseRef.child("mUserInfo").updateChildren(infoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){

                                                                cancel();
                                                                mSavingDialog.cancel();
                                                                mGoogleSignInClient.signOut();
                                                                FirebaseAuth.getInstance().signOut();
                                                                mBaseCallList.clear();
                                                                mBaseMessageList.clear();
                                                                mBaseNoteList.clear();
                                                                mCallAdapter.notifyDataSetChanged();
                                                                mMessageAdapter.notifyDataSetChanged();
                                                                mNoteAdapter.notifyDataSetChanged();
                                                                mIsLogin.edit().putString("is_login", "false").apply();
                                                                Toasty.success(mContext, "Your information updated successfully").show();
                                                                mContext.stopService(new Intent(mContext.getApplicationContext(), MyInfoListenerService.class));
                                                                mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                                                ((ProfileActivity) mContext).finishAffinity();

                                                            }else {
                                                                mSavingDialog.cancel();
                                                                Toasty.error(mContext, task.getException().getMessage()).show();
                                                            }
                                                        }
                                                    });
                                                }

                                            }else {
                                                mSavingDialog.cancel();
                                                Toasty.error(mContext, task.getException().getMessage()).show();
                                            }
                                        }
                                    });

                                }else {
                                    cancel();
                                    mSavingDialog.cancel();
                                    Toasty.error(mContext, task.getException().getMessage()).show();
                                    new MaterialAlertDialogBuilder(mContext)
                                            .setTitle("Authentication has been expired!")
                                            .setMessage("It has been a long time since your last log in. So if you want to change your Email, you have to logout and login first. After that you can delete your account.")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.cancel();
                                                    cancel();
                                                    mSavingDialog.cancel();
                                                    mGoogleSignInClient.signOut();
                                                    FirebaseAuth.getInstance().signOut();
                                                    mBaseCallList.clear();
                                                    mBaseMessageList.clear();
                                                    mBaseNoteList.clear();
                                                    mCallAdapter.notifyDataSetChanged();
                                                    mMessageAdapter.notifyDataSetChanged();
                                                    mNoteAdapter.notifyDataSetChanged();
                                                    mIsLogin.edit().putString("is_login", "false").apply();
                                                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
                                                    ((ProfileActivity) mContext).finishAffinity();
                                                }
                                            }).show();
                                }
                            }
                        });
                    }
                }
                else {
                    String email = mEmailEditTxt.getText().toString().trim();
                    HashMap<String, Object> infoMap = new HashMap<>();
                    infoMap.put("mFirstName", firstName);
                    infoMap.put("mLastName", lastName);
                    infoMap.put("mAllSimNumbers", mAllUserPhoneNumbers);
                    infoMap.put("mEmail", email);

                    if (email.equals(mCurrentUser.getEmail())){
                        mSavingDialog.show();
                        mUserDatabaseRef.child("mUserInfo").updateChildren(infoMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    cancel();
                                    mSavingDialog.cancel();
                                    Toasty.success(mContext, "Your information updated successfully").show();
                                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                                    ((ProfileActivity) mContext).finishAffinity();
                                }else {
                                    mSavingDialog.cancel();
                                    Toasty.error(mContext, task.getException().getMessage()).show();
                                }
                            }
                        });
                    }
                    else {
                        if (firstName.length() == 0 || lastName.length() == 0 || email.length() == 0){
                            Toasty.error(mContext, "All of the fields must be completed").show();
                        }else if (firstName.contains(".") || lastName.contains(".") || email.contains(" ")){
                            Toasty.error(mContext, "Invalid character").show();
                        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            Toasty.error(mContext, "Email is not valid").show();
                        }else {
                            cancel();
                            ChangeEmailDialog changeEmailDialog = new ChangeEmailDialog(mContext, infoMap);
                            changeEmailDialog.show();
                        }
                    }
                }

                break;

            case R.id.edit_info_dialog_cancel:
                mAllUserPhoneNumbers = mCurrentUserInfo.getmAllSimNumbers();
                if (mAccountProviderType.equals("google.com")){
                    if (isClientChanged){
                        new MaterialAlertDialogBuilder(mContext)
                                .setTitle("Login is required")
                                .setMessage("You have been changed your Google Client, so you have to login to your account again")
                                .setCancelable(false)
                                .setPositiveButton("OK", new OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancel();
                                        mGoogleSignInClient.signOut();
                                        FirebaseAuth.getInstance().signOut();
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
                                }).show();
                    }
                    else {
                        cancel();
                    }
                }
                cancel();
                break;

            case R.id.edit_info_dialog_change_email_google:
                mLoadingDialog = new CustomProgressDialog(mContext, "Loading ...");
                mLoadingDialog.setCanceledOnTouchOutside(false);
                mLoadingDialog.setCancelable(false);
                mGoogleSignInClient = GoogleSignIn.getClient(mContext, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(mContext.getString(R.string.default_web_client_id)).requestEmail().build());
                mGoogleSignInClient.signOut();
                isClientChanged = true;
                ((ProfileActivity) mContext).startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
                break;

            case R.id.edit_info_dialog_edit_phone_number:
                InputPhoneNumberDialog inputPhoneNumberDialog = new InputPhoneNumberDialog(mContext, mCurrentUserInfo) {
                    @Override
                    protected void onSaveChanges(ArrayList<String> newNumberList) {
                        if (newNumberList.size() != 0){
                            setNewPhoneNumbers(newNumberList);
                        }
                    }
                };
                inputPhoneNumberDialog.setCanceledOnTouchOutside(false);
                inputPhoneNumberDialog.setCancelable(false);
                inputPhoneNumberDialog.show();
                break;

        }

    }









    private String accountProviderDetector(){
        for (UserInfo userInfo : mCurrentUser.getProviderData()){
            if (userInfo.getProviderId().equals("google.com")){
                return "google.com";
            }else if (userInfo.getProviderId().equals("password")){
                return "password";
            }
        }
        return "unknown";
    }



    void setNewGoogleEmail(GoogleSignInAccount googleSignInAccount){
        this.mGoogleEmailTxv.setText(googleSignInAccount.getEmail());
        this.mNewGoogleSignInAccount = googleSignInAccount;
        this.mLoadingDialog.cancel();
    }



    private void setNewPhoneNumbers(ArrayList<String> newNumberList){
        mNewPhoneNumberList = newNumberList;
        String phoneNumbers = "";
        for (String number : newNumberList){
            if (newNumberList.indexOf(number) == newNumberList.size()-1){
                phoneNumbers = phoneNumbers.concat(number);
            }else {
                phoneNumbers = phoneNumbers.concat(number).concat("\n");
            }
        }
        mPhoneNumberTxv.setText(phoneNumbers);
    }

}
