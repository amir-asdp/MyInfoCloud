package com.ossoft.personalmyinfocloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ossoft.personalmyinfocloud.data.CallItem;
import com.ossoft.personalmyinfocloud.data.ContactItem;
import com.ossoft.personalmyinfocloud.data.MessageItem;
import com.ossoft.personalmyinfocloud.data.NoteItem;
import com.ossoft.personalmyinfocloud.data.UserInfoItem;
import com.ossoft.personalmyinfocloud.data.UserItem;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;

public class SignUpActivity extends AppCompatActivity {

    DatabaseReference mUserDatabaseRef;
    FirebaseAuth mUserAuth;
    FirebaseUser mUser;
    CustomProgressDialog mSignUpProgressDialog;


    EditText mFirstNameEditTxt, mLastNameEditTxt, mEmailEditTxt, mPasswordEditTxt;
    ImageView mPasswordVisibler;
    TextView mEmailErrorTxv, mPasswordErrorTxv, mGoLogin;
    MaterialButton mSignUpBtn;
    boolean visibleFlag = true;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserAuth = FirebaseAuth.getInstance();
        mSignUpProgressDialog = new CustomProgressDialog(SignUpActivity.this, "Authenticating ...");
        mSignUpProgressDialog.setCanceledOnTouchOutside(false);
        mSignUpProgressDialog.setCancelable(false);


        mFirstNameEditTxt = findViewById(R.id.edit_txt_firstname_sign_up);
        mLastNameEditTxt = findViewById(R.id.edit_txt_lastname_sign_up);
        mEmailEditTxt = findViewById(R.id.edit_txt_email_sign_up);
        mPasswordEditTxt = findViewById(R.id.edit_txt_password_sign_up);
        mPasswordVisibler = findViewById(R.id.password_visibler_sign_up_imv);
        mEmailErrorTxv = findViewById(R.id.sign_up_email_error);
        mPasswordErrorTxv = findViewById(R.id.sign_up_password_error);
        mGoLogin = findViewById(R.id.go_login_txv);
        mSignUpBtn = findViewById(R.id.sign_up_btn);



        mEmailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email1 = mEmailEditTxt.getText().toString();
                String email2 = mEmailEditTxt.getText().toString().trim();

                if (!email1.contains(" ")) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                        mEmailErrorTxv.setText("");
                    } else if (email2.length() == 0) {
                        mEmailErrorTxv.setText("");

                    } else {
                        mEmailErrorTxv.setText("Email is not valid");
                    }
                } else {
                    mEmailErrorTxv.setText("There is invalid character");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });



        mPasswordEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = mPasswordEditTxt.getText().toString();
                if (password.length() > 0) {
                    passwordStrengthChecker(mPasswordEditTxt);
                } else {
                    mPasswordErrorTxv.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        mPasswordVisibler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPasswordEditTxt.getText().toString().length() != 0) {
                    if (visibleFlag) {
                        mPasswordEditTxt.setInputType(InputType.TYPE_CLASS_TEXT);
                        mPasswordVisibler.setImageResource(R.drawable.ic_visibility_on);
                        mPasswordEditTxt.setSelection(mPasswordEditTxt.getText().toString().length());
                        visibleFlag = false;
                    } else {
                        mPasswordEditTxt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        mPasswordVisibler.setImageResource(R.drawable.ic_visibility_off);
                        mPasswordEditTxt.setSelection(mPasswordEditTxt.getText().toString().length());
                        visibleFlag = true;
                    }
                }
            }
        });



        mGoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                finishAffinity();
            }
        });



        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstNameEditTxt.getText().toString();
                String lastName = mLastNameEditTxt.getText().toString();
                String email = mEmailEditTxt.getText().toString().trim();
                String password = mPasswordEditTxt.getText().toString().trim();

                if (firstName.length()==0 || lastName.length()==0 || email.length()==0 || password.length()==0){
                    vibrate(200);
                    Toasty.error(SignUpActivity.this, "All of the fields must be completed").show();
                }else if (mEmailErrorTxv.length()==0 && !mPasswordErrorTxv.getText().toString().equals("Password is too Easy") && !mPasswordErrorTxv.getText().toString().equals("There is invalid character")){
                    if (checkInternetConnection(SignUpActivity.this)){
                        mSignUpProgressDialog.show();
                        signUpUser(firstName, lastName, email, password);
                    }
                    else {
                        vibrate(200);
                    }
                }
                else {
                    vibrate(200);
                    String emailError = mEmailErrorTxv.getText().toString();
                    String passwordError = mPasswordErrorTxv.getText().toString();
                    Toasty.error(SignUpActivity.this, (emailError.equals("There is invalid character")||emailError.equals("Email is not valid"))? emailError:passwordError).show();
                }

            }
        });

    }









    public void signUpUser(final String firstName, final String lastName, final String email, String password) {

        mUserAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    mUser = mUserAuth.getCurrentUser();

                    mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                UserItem userItem = initializeUser(mUser.getUid(), firstName, lastName, email, "", new ArrayList<String>(), "true", null, null);
                                mUserDatabaseRef.child(mUser.getUid()).setValue(userItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mUserAuth.signOut();
                                            mSignUpProgressDialog.cancel();

                                            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                    finishAffinity();
                                                }
                                            };

                                            new MaterialAlertDialogBuilder(SignUpActivity.this)
                                                    .setIcon(R.drawable.ic_verification)
                                                    .setTitle("Email Verification")
                                                    .setMessage("Your account created successfully and Verification link was sent to your Email. Please verify your account and then Login")
                                                    .setPositiveButton("OK", dialogClickListener)
                                                    .setCancelable(false)
                                                    .show();

                                        }else {
                                            mSignUpProgressDialog.cancel();
                                            Toasty.error(SignUpActivity.this, task.getException().getMessage()).show();
                                        }
                                    }
                                });

                            }else {
                                mSignUpProgressDialog.cancel();
                                Toasty.error(SignUpActivity.this, "Verification failed").show();
                                mUser.delete();
                            }
                        }
                    });

                }else {
                    mSignUpProgressDialog.cancel();
                    Toasty.error(SignUpActivity.this, task.getException().getMessage()).show();
                }
            }
        });

    }



    public UserItem initializeUser(String UID, String firstName, String lastName, String email, String photoUrl, ArrayList<String> lastLocation, String isFirstSignUp, String activeSimNumber, ArrayList<String> allSimNumbers) {
        ArrayList<CallItem> callList = new ArrayList<>();
        ArrayList<MessageItem> messageList = new ArrayList<>();
        ArrayList<NoteItem> noteList = new ArrayList<>();
        ArrayList<ContactItem> contactList = new ArrayList<>();
        UserInfoItem userInfoItem = new UserInfoItem(UID, firstName, lastName, email, photoUrl, lastLocation, isFirstSignUp, activeSimNumber, allSimNumbers);
        UserItem userItem = new UserItem(callList, messageList, noteList, contactList, userInfoItem);
        return userItem;
    }



    public void passwordStrengthChecker(EditText s) {
        int len = s.getText().toString().length();
        if (!s.getText().toString().contains(" ")) {
            mPasswordErrorTxv.setText("");
            if (len < 8) {
                mPasswordErrorTxv.setText("Password is too Easy");
                mPasswordErrorTxv.setTextColor(getResources().getColor(R.color.red_A700));
            } else if (len < 15) {
                mPasswordErrorTxv.setText("Password is strong");
                mPasswordErrorTxv.setTextColor(getResources().getColor(R.color.indigo_600));
            } else {
                mPasswordErrorTxv.setText("Password is very strong");
                mPasswordErrorTxv.setTextColor(getResources().getColor(R.color.lightGreen_A700));
            }

            if (len == 30) {
                mPasswordErrorTxv.setText("Password Max Length Reached");
                mPasswordErrorTxv.setTextColor(getResources().getColor(R.color.lightGreen_A700));
            }
        }else {
            mPasswordErrorTxv.setText("There is invalid character");
            mPasswordErrorTxv.setTextColor(getResources().getColor(R.color.red_A700));
        }
    }



    public boolean checkInternetConnection(Context context){
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toasty.warning(context, "No internet connection").show();
            return false;
        }
        return true;
    }



    public void vibrate(int duration) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(duration);
    }

}
