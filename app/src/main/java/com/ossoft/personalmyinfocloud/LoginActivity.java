package com.ossoft.personalmyinfocloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

import static com.ossoft.personalmyinfocloud.StartupActivity.mIsLogin;

public class LoginActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 101;
    public static GoogleSignInClient mGoogleSignInClient;
    DatabaseReference mUserDatabaseRef;
    FirebaseAuth mUserAuth;
    FirebaseUser mUser;
    CustomProgressDialog mLoginProgressDialog;
    AlertDialog mLoginVerificationDialog;

    EditText mEmailEditTxt, mPasswordEditTxt;
    ImageView mPasswordVisibler;
    TextView mEmailErrorTxv, mPasswordErrorTxv, mGoSignUp;
    MaterialButton mLoginBtn, mGoogleSignInBtn;
    boolean visibleFlag = true;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            mLoginProgressDialog.cancel();
            mLoginProgressDialog = new CustomProgressDialog(LoginActivity.this, "Logging in ...");
            mLoginProgressDialog.setCancelable(false);
            mLoginProgressDialog.show();
            try {
                loginUserWithGoogle(GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class));
            } catch (ApiException e) {
                Toasty.error(this, e.getMessage()).show();
                loginUserWithGoogle( null);
            }
        } else {
            mLoginProgressDialog.cancel();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUserAuth = FirebaseAuth.getInstance();
        mGoogleSignInClient = GoogleSignIn.getClient(this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());
        mLoginProgressDialog = new CustomProgressDialog(LoginActivity.this, "Loading ...");
        mLoginProgressDialog.setCanceledOnTouchOutside(false);
        mLoginProgressDialog.setCancelable(false);


        mEmailEditTxt = findViewById(R.id.edit_txt_email_login);
        mPasswordEditTxt = findViewById(R.id.edit_txt_password_login);
        mPasswordVisibler = findViewById(R.id.password_visibler_login_imv);
        mEmailErrorTxv = findViewById(R.id.login_email_error);
        mPasswordErrorTxv = findViewById(R.id.login_password_error);
        mGoSignUp = findViewById(R.id.go_sign_up_txv);
        mLoginBtn = findViewById(R.id.login_btn);
        mGoogleSignInBtn = findViewById(R.id.login_google_btn);



        mEmailEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String email1=mEmailEditTxt.getText().toString();
                String email2=mEmailEditTxt.getText().toString().trim();

                if (!email1.contains(" ")) {
                    if (Patterns.EMAIL_ADDRESS.matcher(email2).matches()) {
                        mEmailErrorTxv.setText("");
                    } else if (email2.length() == 0) {
                        mEmailErrorTxv.setText("");

                    } else {
                        mEmailErrorTxv.setText("Email is not valid");
                    }
                }else {
                    mEmailErrorTxv.setText("There is invalid character");
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });



        mPasswordEditTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = mPasswordEditTxt.getText().toString();
                if (password.length() > 0) {
                    if (password.contains(" ")){
                        mPasswordErrorTxv.setText("There is invalid character");
                    }else {
                        mPasswordErrorTxv.setText("");
                    }
                }
                else {
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



        mGoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });



        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmailEditTxt.getText().toString().trim();
                String password = mPasswordEditTxt.getText().toString().trim();

                if (email.length()==0 || password.length()==0){
                    vibrate(200);
                    Toasty.error(LoginActivity.this, "All of the fields must be completed").show();
                }else if (mEmailErrorTxv.length()==0 && mPasswordErrorTxv.length() == 0){
                    if (checkInternetConnection(LoginActivity.this)){
                        mLoginProgressDialog = new CustomProgressDialog(LoginActivity.this, "Logging in ...");
                        mLoginProgressDialog.setCancelable(false);
                        mLoginProgressDialog.show();
                        loginUser(email, password);
                    }
                    else {
                        vibrate(200);
                    }
                }
                else {
                    vibrate(200);
                    String emailError = mEmailErrorTxv.getText().toString();
                    String passwordError = mPasswordErrorTxv.getText().toString();
                    Toasty.error(LoginActivity.this, (emailError.equals("There is invalid character")||emailError.equals("Email is not valid"))? emailError:passwordError).show();
                }
            }
        });



        mGoogleSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection(LoginActivity.this)){
                    mLoginProgressDialog.setCancelable(false);
                    mLoginProgressDialog.show();
                    startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
                }
            }
        });

    }









    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }



    public void loginUser(String email, String password){

        mUserAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mUser = mUserAuth.getCurrentUser();
                    if (mUser.isEmailVerified()){
                        mIsLogin.edit().putString("is_login", "true").apply();
                        mLoginProgressDialog.cancel();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                        Toasty.success(LoginActivity.this, "Logged in successfully").show();
                    }else {

                        mLoginProgressDialog.cancel();

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE){
                                    mLoginVerificationDialog.cancel();
                                }else {
                                    mLoginVerificationDialog.cancel();
                                    mUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toasty.success(LoginActivity.this, "Verification Email was sent successfully").show();
                                            }else {
                                                Toasty.error(LoginActivity.this, task.getException().getMessage()).show();
                                            }
                                        }
                                    });
                                }

                            }
                        };

                        mLoginVerificationDialog = new MaterialAlertDialogBuilder(LoginActivity.this)
                                .setIcon(R.drawable.ic_verification)
                                .setTitle("Email Verification")
                                .setMessage("Your account hasn't been verified yet. Please verify your account and then Login")
                                .setPositiveButton("OK", dialogClickListener)
                                .setNeutralButton("Send verification Email", dialogClickListener)
                                .setCancelable(false)
                                .show();

                        mUserAuth.signOut();

                    }
                }else {
                    mLoginProgressDialog.cancel();
                    Toasty.error(LoginActivity.this, task.getException().getMessage()).show();
                }
            }
        });

    }



    public void loginUserWithGoogle(GoogleSignInAccount account){
        mUserAuth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    mUser = mUserAuth.getCurrentUser();

                    if (task.getResult().getAdditionalUserInfo().isNewUser()){
                        UserItem userItem = initializeUser(mUser.getUid(), mUser.getDisplayName().split(" ")[0],
                                (mUser.getDisplayName().split(" ")[1]==null)?"":mUser.getDisplayName().split(" ")[1], mUser.getEmail(), (mUser.getPhotoUrl()==null) ? "":mUser.getPhotoUrl().toString(), new ArrayList<String>(), "true", null, null);
                        mUserDatabaseRef.child(mUser.getUid()).setValue(userItem).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    mIsLogin.edit().putString("is_login", "true").apply();
                                    mLoginProgressDialog.cancel();
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finishAffinity();
                                    Toasty.success(LoginActivity.this, "Signed up successfully!").show();
                                }else {
                                    mLoginProgressDialog.cancel();
                                    Toasty.error(LoginActivity.this, task.getException().getMessage()).show();
                                }
                            }
                        });
                    }else {
                        mIsLogin.edit().putString("is_login", "true").apply();
                        mLoginProgressDialog.cancel();
                        startActivity(new Intent(LoginActivity.this ,MainActivity.class));
                        finishAffinity();
                        Toasty.success(LoginActivity.this, "Logged in successfully!").show();
                    }
                }else {
                    mLoginProgressDialog.cancel();
                    Toasty.error(LoginActivity.this, task.getException().getMessage()).show();
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
