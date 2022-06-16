package com.ossoft.personalmyinfocloud;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.Settings;
import android.provider.Telephony;
import android.telephony.SubscriptionManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.ossoft.personalmyinfocloud.data.CallAdapter;
import com.ossoft.personalmyinfocloud.data.CallItem;
import com.ossoft.personalmyinfocloud.data.CallPersonItem;
import com.ossoft.personalmyinfocloud.data.ContactItem;
import com.ossoft.personalmyinfocloud.data.MessageAdapter;
import com.ossoft.personalmyinfocloud.data.MessageItem;
import com.ossoft.personalmyinfocloud.data.MessagePersonItem;
import com.ossoft.personalmyinfocloud.data.NoteAdapter;
import com.ossoft.personalmyinfocloud.data.NoteItem;
import com.ossoft.personalmyinfocloud.data.SearchItemAdapter;
import com.ossoft.personalmyinfocloud.data.UserInfoItem;
import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.entity.Email;
import com.tomash.androidcontacts.contactgetter.entity.PhoneNumber;
import com.tomash.androidcontacts.contactgetter.main.FieldType;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import github.nisrulz.easydeviceinfo.base.EasyDeviceMod;
import github.nisrulz.easydeviceinfo.base.EasyLocationMod;

import static androidx.fragment.app.FragmentPagerAdapter.BEHAVIOR_SET_USER_VISIBLE_HINT;
import static com.ossoft.personalmyinfocloud.LoginActivity.mGoogleSignInClient;
import static com.ossoft.personalmyinfocloud.StartupActivity.mIsLogin;

public class MainActivity extends AppCompatActivity {

    public static FirebaseAuth mUserAuth;
    public static FirebaseUser mCurrentUser;
    public static DatabaseReference mUserDatabaseRef;
    public static StorageReference mContactPhotoStorageRef;
    public static StorageReference mProfilePhotoStorageRef;
    public static UserInfoItem mUserInfoItem = new UserInfoItem();
    public static CallAdapter mCallAdapter;
    public static MessageAdapter mMessageAdapter;
    public static NoteAdapter mNoteAdapter;
    public static ArrayList<ContactItem> mInitialContactList = new ArrayList<>();
    public static ArrayList<CallItem> mInitialCallList = new ArrayList<>();
    public static ArrayList<MessageItem> mInitialMessageList = new ArrayList<>();
    public static ArrayList<CallItem> mBaseCallList = new ArrayList<>();
    public static ArrayList<MessageItem> mBaseMessageList = new ArrayList<>();
    public static ArrayList<NoteItem> mBaseNoteList = new ArrayList<>();
    public static ArrayList<String> mAllUserPhoneNumbers = new ArrayList<>();
    public static String mActivePhoneNumber;
    public static ValueEventListener mUserInfoEventListener;
    public static ValueEventListener mCallEventListener;
    public static ValueEventListener mMessageEventListener;
    public static ValueEventListener mNoteEventListener;
    SearchItemAdapter mSearchItemAdapter;
    ArrayList<Object> mSearchItemList = new ArrayList<>();
    CustomProgressDialog mLoadingDataDialog;
    CustomProgressDialog mUploadingDataDialog;
    CustomProgressDialog mLoadingDialog;
    AlertDialog mPermissionDialog;
    CustomCallBack mEventFlagCallBack;
    SubscriptionManager mSubscriptionManager;
    PhoneNumberOfflineGeocoder mGeocoder;
    PhoneNumberUtil mPhoneUtil;
    LocationRequest mLocationRequest;
    public static EasyLocationMod mEasyLocationMod;

    MaterialToolbar mToolbar;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    ActionBarDrawerToggle mToggle;
    LinearLayout mChipGroupContainer;
    ChipGroup mFilterChipGroup;
    Chip mCallChip, mMessageChip, mNoteChip;
    TabLayout mTabLayout;
    ViewPager mViewPager;
    RecyclerView mSearchRecyclerView;
    View mNavigationHeaderView;
    CircleImageView mNavigationHeaderPic;
    TextView mNavigationHeaderFullName, mNavigationHeaderUID;
    ExtendedFloatingActionButton mNewNoteButton;

    public static boolean mAreAllPermissionsGranted = false;
    public static boolean mAreCallsLoaded = false;
    public static boolean mAreMessagesLoaded = false;
    public static boolean mAreNotesLoaded = false;
    boolean mFirstSignUpFlag = true;
    int REQUEST_CHECK_SETTINGS_CODE = 201;
    String ACCESS_FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION;
    String READ_PHONE_NUMBERS_PERMISSION = Manifest.permission.READ_PHONE_NUMBERS;
    String READ_PHONE_STATE_PERMISSION = Manifest.permission.READ_PHONE_STATE;
    String WRITE_CONTACTS_PERMISSION = Manifest.permission.WRITE_CONTACTS;
    String READ_CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS;
    String READ_CALL_LOG_PERMISSION = Manifest.permission.READ_CALL_LOG;
    String READ_SMS_PERMISSION = Manifest.permission.READ_SMS;
    String RECEIVE_SMS_PERMISSION = Manifest.permission.RECEIVE_SMS;
    String GET_ACCOUNTS_PERMISSION = Manifest.permission.GET_ACCOUNTS;
    String ACCESS_COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION;



    @SuppressLint("MissingPermission")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        if (requestCode == 101){
            requestPermission();
        }else if (requestCode == REQUEST_CHECK_SETTINGS_CODE){
            switch (resultCode){
                case RESULT_OK:
                    ArrayList<String> locationArray = new ArrayList<>();
                    locationArray.add(String.valueOf(mEasyLocationMod.getLatLong()[0]));
                    locationArray.add(String.valueOf(mEasyLocationMod.getLatLong()[1]));
                    mUserDatabaseRef.child("mUserInfo").child("mLastLocation").setValue(locationArray);
                    break;
                case RESULT_CANCELED:
                    Toasty.error(MainActivity.this, "Access rejected. Failed to find location").show();
                    break;
            }
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserAuth = FirebaseAuth.getInstance();
        mCurrentUser = mUserAuth.getCurrentUser();
        mUserDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mContactPhotoStorageRef = FirebaseStorage.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("ContactPhotos");
        mProfilePhotoStorageRef = FirebaseStorage.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("ProfilePhoto");
        mGeocoder = PhoneNumberOfflineGeocoder.getInstance();
        mPhoneUtil = PhoneNumberUtil.getInstance();
        mCallAdapter = new CallAdapter(MainActivity.this, mBaseCallList);
        mMessageAdapter = new MessageAdapter(MainActivity.this, mBaseMessageList);
        mNoteAdapter = new NoteAdapter(MainActivity.this, mBaseNoteList);
        mSearchItemAdapter= new SearchItemAdapter(MainActivity.this, mSearchItemList);



        if (mIsLogin == null){
            mIsLogin = getSharedPreferences("is_login", MODE_PRIVATE);
        }



        mEventFlagCallBack = new CustomCallBack() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
            @SuppressLint("MissingPermission")
            @Override
            public void onMessage(String action, String message) {
                if (action.equals("granted")){
                    ContextCompat.startForegroundService(getApplicationContext(), new Intent(getApplicationContext(), MyInfoListenerService.class));

                    mLocationRequest = createLocationRequest();
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                    Task<LocationSettingsResponse> task = LocationServices.getSettingsClient(MainActivity.this).checkLocationSettings(builder.build());
                    task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                            mEasyLocationMod = new EasyLocationMod(MainActivity.this);
                            ArrayList<String> locationArray = new ArrayList<>();
                            locationArray.add(String.valueOf(mEasyLocationMod.getLatLong()[0]));
                            locationArray.add(String.valueOf(mEasyLocationMod.getLatLong()[1]));
                            mUserDatabaseRef.child("mUserInfo").child("mLastLocation").setValue(locationArray);
                        }
                    });
                    task.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof ResolvableApiException) {
                                // Location settings are not satisfied, but this can be fixed
                                // by showing the user a dialog.
                                try {
                                    // Show the dialog by calling startResolutionForResult(),
                                    // and check the result in onActivityResult().
                                    ResolvableApiException resolvable = (ResolvableApiException) e;
                                    resolvable.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS_CODE);
                                } catch (IntentSender.SendIntentException sendEx) {
                                    // Ignore the error.
                                }
                            }
                        }
                    });
                }

                if (action.equals("loaded")){
                    switch (message){
                        case "call": mAreCallsLoaded = true; break;
                        case "message": mAreMessagesLoaded = true; break;
                        case "note": mAreNotesLoaded = true; break;
                    }

                    if (mAreCallsLoaded && mAreMessagesLoaded && mAreNotesLoaded){
                        mAreCallsLoaded = false;
                        mAreMessagesLoaded = false;
                        mAreNotesLoaded = false;
                        mLoadingDataDialog.cancel();
                    }
                }
            }
        };



        if (checkInternetConnection(MainActivity.this)){
            mLoadingDataDialog = new CustomProgressDialog(MainActivity.this, "Loading ...");
            mLoadingDataDialog.setCanceledOnTouchOutside(false);
            mLoadingDataDialog.setCancelable(false);
            mLoadingDataDialog.show();

            mUserInfoEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue(UserInfoItem.class) != null){
                        mUserInfoItem = snapshot.getValue(UserInfoItem.class);
                        Glide.with(MainActivity.this).load(mUserInfoItem.getmPhotoUrl()).placeholder(R.drawable.ic_account).fitCenter().into(mNavigationHeaderPic);
                        mNavigationHeaderFullName.setText(mUserInfoItem.getmFirstName() + " " + mUserInfoItem.getmLastName());
                        mNavigationHeaderUID.setText("UID: " + mUserInfoItem.getmUID());
                        mAllUserPhoneNumbers = mUserInfoItem.getmAllSimNumbers();
                    }

                    if (mAreAllPermissionsGranted && mUserInfoItem != null && mUserInfoItem.getmIsFirstSignUp().equals("true") && mFirstSignUpFlag){
                        mFirstSignUpFlag = false;
                        mLoadingDataDialog.cancel();
                        mUploadingDataDialog = new CustomProgressDialog(MainActivity.this, "Initializing ...");
                        mUploadingDataDialog.setCanceledOnTouchOutside(false);
                        mUploadingDataDialog.setCancelable(false);
                        mUploadingDataDialog.show();

                        InputPhoneNumberDialog inputPhoneNumberDialog = new InputPhoneNumberDialog(MainActivity.this) {
                            @Override
                            protected void onSaveChanges(ArrayList<String> newNumberList) {
                                new initializerAsyncTask().execute();
                            }
                        };
                        inputPhoneNumberDialog.show();
                        inputPhoneNumberDialog.setCanceledOnTouchOutside(false);
                        inputPhoneNumberDialog.setCancelable(false);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mLoadingDataDialog.cancel();
                    Toasty.error(MainActivity.this, error.getMessage()).show();
                }
            };

            mCallEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null){
                        mBaseCallList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            mBaseCallList.add(dataSnapshot.getValue(CallItem.class));
                            mCallAdapter.notifyDataSetChanged();
                        }
                    }
                    mEventFlagCallBack.onMessage("loaded", "call");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mLoadingDataDialog.cancel();
                    Toasty.error(MainActivity.this, error.getMessage()).show();
                }
            };

            mMessageEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue() != null){
                        mBaseMessageList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            mBaseMessageList.add(dataSnapshot.getValue(MessageItem.class));
                            mMessageAdapter.notifyDataSetChanged();
                        }
                    }
                    mEventFlagCallBack.onMessage("loaded", "message");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mLoadingDataDialog.cancel();
                    Toasty.error(MainActivity.this, error.getMessage()).show();
                }
            };

            mNoteEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    mBaseNoteList.clear();
                    if (snapshot.getValue() != null){
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            mBaseNoteList.add(dataSnapshot.getValue(NoteItem.class));
                            mNoteAdapter.notifyDataSetChanged();
                        }
                    }
                    mNoteAdapter.notifyDataSetChanged();
                    mEventFlagCallBack.onMessage("loaded", "note");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    mLoadingDataDialog.cancel();
                    Toasty.error(MainActivity.this, error.getMessage()).show();
                }
            };

        }



        requestPermission();



        mToolbar = findViewById(R.id.toolbar); setSupportActionBar(mToolbar); getSupportActionBar().setDisplayShowTitleEnabled(false);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mNavigationView = findViewById(R.id.navigation_view);
        mChipGroupContainer = findViewById(R.id.filter_chip_group_container);
        mFilterChipGroup = findViewById(R.id.filter_chip_group);
        mCallChip = findViewById(R.id.chip_call);
        mMessageChip = findViewById(R.id.chip_message);
        mNoteChip = findViewById(R.id.chip_note);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPager = findViewById(R.id.view_pager);
        mSearchRecyclerView = findViewById(R.id.search_recycler_view);
        mNewNoteButton = findViewById(R.id.add_note_fab);
        mNavigationHeaderView = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        mNavigationHeaderPic = mNavigationHeaderView.findViewById(R.id.header_profile_pic);
        mNavigationHeaderFullName = mNavigationHeaderView.findViewById(R.id.header_full_name);
        mNavigationHeaderUID = mNavigationHeaderView.findViewById(R.id.header_UID);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), BEHAVIOR_SET_USER_VISIBLE_HINT);
        mViewPager.setAdapter(viewPagerAdapter);
        mSearchRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mSearchRecyclerView.setAdapter(mSearchItemAdapter);
        mTabLayout.setupWithViewPager(mViewPager, true);

        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_calls); mTabLayout.getTabAt(0).setText(R.string.calls);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_messages); mTabLayout.getTabAt(1).setText(R.string.messages);
        mTabLayout.getTabAt(2).setIcon(R.drawable.ic_notes); mTabLayout.getTabAt(2).setText(R.string.notes);
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0){
                    mNewNoteButton.setVisibility(View.GONE);
                }else if (position == 1){
                    mNewNoteButton.setVisibility(View.GONE);
                }else if (position == 2){
                    mNewNoteButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){

                    case R.id.profile_btn:
                        startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                        break;


                    case R.id.logout_btn:

                        DialogInterface.OnClickListener logoutClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE){
                                    mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());
                                    mUserAuth.signOut();
                                    mIsLogin.edit().putString("is_login", "false").apply();
                                    if (mGoogleSignInClient != null){
                                        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    mBaseCallList.clear();
                                                    mBaseMessageList.clear();
                                                    mBaseNoteList.clear();
                                                    mCallAdapter.notifyDataSetChanged();
                                                    mMessageAdapter.notifyDataSetChanged();
                                                    mNoteAdapter.notifyDataSetChanged();
                                                    stopService(new Intent(getApplicationContext(), MyInfoListenerService.class));
                                                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                    finishAffinity();
                                                }else {
                                                    Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                                }
                                            }
                                        });
                                    }else {
                                        mBaseCallList.clear();
                                        mBaseMessageList.clear();
                                        mBaseNoteList.clear();
                                        mCallAdapter.notifyDataSetChanged();
                                        mMessageAdapter.notifyDataSetChanged();
                                        mNoteAdapter.notifyDataSetChanged();
                                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                        finishAffinity();
                                    }
                                    dialog.cancel();
                                }else {
                                    dialog.cancel();
                                }
                            }
                        };

                        new MaterialAlertDialogBuilder(MainActivity.this)
                                .setIcon(R.drawable.ic_logout_black)
                                .setTitle("Log out")
                                .setMessage("Do you want to log out?")
                                .setPositiveButton("Yes", logoutClickListener)
                                .setNeutralButton("Cancel", logoutClickListener)
                                .show();

                        break;


                    case R.id.delete_account_btn:

                        DialogInterface.OnClickListener deleteAccountClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE){


                                    for (UserInfo userInfo : FirebaseAuth.getInstance().getCurrentUser().getProviderData()){
                                        if (userInfo.getProviderId().equals("google.com")){

                                            mLoadingDialog = new CustomProgressDialog(MainActivity.this, "Deleting your account ...");
                                            mLoadingDialog.setCanceledOnTouchOutside(false);
                                            mLoadingDialog.setCancelable(false);
                                            mLoadingDialog.show();
                                            mCurrentUser.getIdToken(false).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<GetTokenResult> task) {
                                                    if (task.isSuccessful()){
                                                        AuthCredential credential = GoogleAuthProvider.getCredential(GoogleSignIn.getLastSignedInAccount(MainActivity.this).getIdToken(), null);
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
                                                                                            mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());
                                                                                            if (mGoogleSignInClient != null){
                                                                                                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()){
                                                                                                            mLoadingDialog.cancel();
                                                                                                            dialog.cancel();
                                                                                                            mIsLogin.edit().putString("is_login", "false").apply();
                                                                                                            mBaseCallList.clear();
                                                                                                            mBaseMessageList.clear();
                                                                                                            mBaseNoteList.clear();
                                                                                                            mCallAdapter.notifyDataSetChanged();
                                                                                                            mMessageAdapter.notifyDataSetChanged();
                                                                                                            mNoteAdapter.notifyDataSetChanged();
                                                                                                            stopService(new Intent(getApplicationContext(), MyInfoListenerService.class));
                                                                                                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                                                                            finishAffinity();
                                                                                                            Toasty.success(MainActivity.this, "Your account deleted successfully").show();
                                                                                                        }else {
                                                                                                            Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                                                                                        }
                                                                                                    }
                                                                                                });
                                                                                            }else {
                                                                                                mLoadingDialog.cancel();
                                                                                                dialog.cancel();
                                                                                                mIsLogin.edit().putString("is_login", "false").apply();
                                                                                                mBaseCallList.clear();
                                                                                                mBaseMessageList.clear();
                                                                                                mBaseNoteList.clear();
                                                                                                mCallAdapter.notifyDataSetChanged();
                                                                                                mMessageAdapter.notifyDataSetChanged();
                                                                                                mNoteAdapter.notifyDataSetChanged();
                                                                                                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                                                                                                finishAffinity();
                                                                                                Toasty.success(MainActivity.this, "Your account deleted successfully").show();
                                                                                            }
                                                                                        }else {
                                                                                            mLoadingDialog.cancel();
                                                                                            Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }else {
                                                                                mLoadingDialog.cancel();
                                                                                Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                                                            }
                                                                        }
                                                                    });

                                                                }else {
                                                                    mLoadingDialog.cancel();
                                                                    Toasty.error(MainActivity.this, task.getException().getMessage(), Toasty.LENGTH_LONG).show();
                                                                    new MaterialAlertDialogBuilder(MainActivity.this)
                                                                            .setTitle("Authentication has been expired!")
                                                                            .setMessage("It has been a long time since your last log in. So if you want to delete your account, you have to logout and login first. After that you can delete your account.")
                                                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                                @Override
                                                                                public void onClick(DialogInterface dialog, int which) {
                                                                                    dialog.cancel();
                                                                                }
                                                                            }).show();
                                                                }
                                                            }
                                                        });
                                                    }else {
                                                        mLoadingDialog.cancel();
                                                        Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                                    }
                                                }
                                            });
                                            break;

                                        }else if (userInfo.getProviderId().equals("password")){
                                            DeleteAccountDialog deleteAccountDialog = new DeleteAccountDialog(MainActivity.this, MainActivity.this);
                                            deleteAccountDialog.show();
                                            break;
                                        }
                                    }


                                }
                                else {
                                    dialog.cancel();
                                }
                            }
                        };

                        new MaterialAlertDialogBuilder(MainActivity.this)
                                .setIcon(R.drawable.ic_delete_account_black)
                                .setTitle("Delete Account")
                                .setMessage("By deleting account, all your information will remove. Do you really want to delete your account? ")
                                .setPositiveButton("Yes", deleteAccountClickListener)
                                .setNeutralButton("Cancel", deleteAccountClickListener)
                                .show();

                        break;


                    case R.id.exit_btn:
                        finishAffinity();
                        break;

                }

                return false;
            }
        });



        mNewNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewNoteDialog addNewNoteDialog = new AddNewNoteDialog(MainActivity.this);
                addNewNoteDialog.show();
            }
        });


    }










    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }



    @Override
    protected void onResume() {
        super.onResume();
        if (checkInternetConnection(MainActivity.this)){
            mUserDatabaseRef.child("mUserInfo").addValueEventListener(mUserInfoEventListener);
            mUserDatabaseRef.child("mCallList").orderByChild("mCallId").addValueEventListener(mCallEventListener);
            mUserDatabaseRef.child("mMessageList").orderByChild("mMessageId").addValueEventListener(mMessageEventListener);
            mUserDatabaseRef.child("mNoteList").orderByChild("mNoteId").addValueEventListener(mNoteEventListener);
        }
    }



    @Override
    protected void onPause() {
        super.onPause();
        mUserDatabaseRef.child("mUserInfo").removeEventListener(mUserInfoEventListener);
        mUserDatabaseRef.child("mCallList").orderByChild("mCallId").removeEventListener(mCallEventListener);
        mUserDatabaseRef.child("mMessageList").orderByChild("mMessageId").removeEventListener(mMessageEventListener);
        mUserDatabaseRef.child("mNoteList").orderByChild("mNoteId").removeEventListener(mNoteEventListener);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar ,menu);
        final SearchView searchView = ((SearchView) menu.findItem(R.id.search_btn).getActionView());
        searchView.setQueryHint("Search Phone number, Name or Note");


        menu.findItem(R.id.search_btn).setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mViewPager.setVisibility(View.GONE);
                mTabLayout.setVisibility(View.GONE);
                mNewNoteButton.setVisibility(View.GONE);
                mChipGroupContainer.setVisibility(View.VISIBLE);
                mSearchRecyclerView.setVisibility(View.VISIBLE);

                final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {

                        mSearchItemList.clear();

                        if (!newText.equals("")){
                            if (mCallChip.isChecked()){
                                for (CallItem callItem : mBaseCallList){
                                    if (callItem.getmCallPerson().getmCallNumber().toLowerCase().contains(newText.toLowerCase()) || callItem.getmCallPerson().getmContactName().toLowerCase().contains(newText.toLowerCase())){
                                        mSearchItemList.add(callItem);
                                    }
                                }
                            }

                            if (mMessageChip.isChecked()){
                                for (MessageItem messageItem : mBaseMessageList){
                                    if (messageItem.getmMessagePerson().getmMessageNumber() != null){
                                        if (messageItem.getmMessagePerson().getmMessageNumber().toLowerCase().contains(newText.toLowerCase()) || messageItem.getmMessagePerson().getmContactName().toLowerCase().contains(newText.toLowerCase())){
                                            mSearchItemList.add(messageItem);
                                        }
                                    }else {
                                        Log.e("--------", String.valueOf(mBaseCallList.indexOf(messageItem)));
                                    }
                                }
                            }

                            if (mNoteChip.isChecked()){
                                for (NoteItem noteItem : mBaseNoteList){
                                    if (noteItem.getmSubject().toLowerCase().contains(newText.toLowerCase()) || noteItem.getmText().toLowerCase().contains(newText.toLowerCase())){
                                        mSearchItemList.add(noteItem);
                                    }
                                }
                            }
                        }

                        mSearchItemAdapter.notifyDataSetChanged();

                        return true;
                    }
                };

                mCallChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        queryTextListener.onQueryTextChange(searchView.getQuery().toString());
                    }
                });

                mMessageChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        queryTextListener.onQueryTextChange(searchView.getQuery().toString());
                    }
                });

                mNoteChip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        queryTextListener.onQueryTextChange(searchView.getQuery().toString());
                    }
                });

                searchView.setOnQueryTextListener(queryTextListener);

                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mViewPager.setVisibility(View.VISIBLE);
                mTabLayout.setVisibility(View.VISIBLE);
                if (mTabLayout.getSelectedTabPosition() == 2){
                    mNewNoteButton.setVisibility(View.VISIBLE);
                }
                mChipGroupContainer.setVisibility(View.GONE);
                mSearchRecyclerView.setVisibility(View.GONE);
                mSearchItemList.clear();
                mSearchItemAdapter.notifyDataSetChanged();
                return true;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }



    public boolean checkInternetConnection(Context context){
        NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            Toasty.warning(context, "No internet connection").show();
            return false;
        }
        return true;
    }



    public void requestPermission(){
        String[] permissionList;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            permissionList = new String[]{ACCESS_FINE_LOCATION_PERMISSION, ACCESS_COARSE_LOCATION_PERMISSION, READ_PHONE_NUMBERS_PERMISSION, READ_PHONE_STATE_PERMISSION, WRITE_CONTACTS_PERMISSION, READ_CONTACTS_PERMISSION, READ_CALL_LOG_PERMISSION, READ_SMS_PERMISSION, RECEIVE_SMS_PERMISSION, GET_ACCOUNTS_PERMISSION};
        }else {
            permissionList = new String[]{ACCESS_FINE_LOCATION_PERMISSION, ACCESS_COARSE_LOCATION_PERMISSION, READ_PHONE_STATE_PERMISSION, WRITE_CONTACTS_PERMISSION, READ_CONTACTS_PERMISSION, READ_CALL_LOG_PERMISSION, READ_SMS_PERMISSION, RECEIVE_SMS_PERMISSION, GET_ACCOUNTS_PERMISSION};
        }
        Dexter.withContext(MainActivity.this)
                .withPermissions(permissionList)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()){
                            mAreAllPermissionsGranted = true;
                            mEventFlagCallBack.onMessage("granted", "");
                        }

                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()){
                            mAreAllPermissionsGranted = false;
                            Log.e("-------", multiplePermissionsReport.getDeniedPermissionResponses().get(0).getPermissionName());
                            openPermissionSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError dexterError) {
                        Toasty.error(MainActivity.this, dexterError.toString()).show();
                    }
                })
                .onSameThread()
                .check();

    }



    public void openPermissionSettings(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_POSITIVE){
                    mPermissionDialog.cancel();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivityForResult(intent, 101);
                }else {
                    mPermissionDialog.cancel();
                    finishAffinity();
                }
            }
        };

        mPermissionDialog = new MaterialAlertDialogBuilder(MainActivity.this)
                .setIcon(R.drawable.ic_permission)
                .setTitle("Permission is required")
                .setMessage("In order to backup your information (Call log, SMS etc ...), please allow the permissions.")
                .setPositiveButton("Go to settings", dialogClickListener)
                .setNeutralButton("Cancel and Exit", dialogClickListener)
                .setCancelable(false)
                .show();
    }



    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }



    public String dateFormatter(long timeMillis){
        return DateFormat.format("dd/MM/yyyy\nHH:mm", timeMillis).toString();
    }



    public String phoneNumberFormatter(String phoneNumber, PhoneNumberUtil.PhoneNumberFormat requiredFormat){

        switch (requiredFormat){
            case INTERNATIONAL:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
            case NATIONAL:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.NATIONAL).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
            case E164:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.E164).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
            case RFC3966:
                try {
                    return mPhoneUtil.format(mPhoneUtil.parse(phoneNumber, "GR"), PhoneNumberUtil.PhoneNumberFormat.RFC3966).replaceAll(" ", "");
                } catch (NumberParseException e) {
                    e.printStackTrace();
                }
                break;
        }

        return "";
    }



    interface CustomCallBack {

        void onMessage(String action, String message);

    }



    public class initializerAsyncTask extends AsyncTask<Void, String, Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            mInitialContactList = initContactList();
            mInitialMessageList = initMessageList();
            mInitialCallList = initCallList();

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            mUploadingDataDialog.setProgressText(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            mUploadingDataDialog.setProgressText("Uploading Contacts ...");
            mUserDatabaseRef.child("mContactList").setValue(mInitialContactList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){

                        mUploadingDataDialog.setProgressText("Uploading Call logs ...");
                        mUserDatabaseRef.child("mCallList").setValue(mInitialCallList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){

                                    mUploadingDataDialog.setProgressText("Uploading Messages ...");
                                    mUserDatabaseRef.child("mMessageList").setValue(mInitialMessageList).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){

                                                mUserDatabaseRef.child("mUserInfo").child("mIsFirstSignUp").setValue("false").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()){
                                                            mUploadingDataDialog.cancel();
                                                        }else {
                                                            mUploadingDataDialog.cancel();
                                                            Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                                        }
                                                    }
                                                });

                                            }else {
                                                Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                            }
                                        }
                                    });

                                }else {
                                    Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                                }
                            }
                        });

                    }else {
                        Toasty.error(MainActivity.this, task.getException().getMessage()).show();
                    }
                }
            });

        }



        ArrayList<ContactItem> initContactList() {

            ArrayList<ContactItem> contactList = new ArrayList<>();

            List<ContactData> rawContactList = new ContactsGetterBuilder(MainActivity.this)
                    .addField(FieldType.NAME_DATA, FieldType.PHONE_NUMBERS, FieldType.EMAILS)
                    .buildList();

            for (ContactData contactData : rawContactList){
                final String[] contactPhotoUrl = {""};
                String contactName = contactData.getNameData().getFullName();
                ArrayList<String> contactNumbers = new ArrayList<>();
                ArrayList<String> contactEmails = new ArrayList<>();

                for (PhoneNumber phoneNumber : contactData.getPhoneList()){
                    if (!contactNumbers.contains(phoneNumber.getMainData().replaceAll(" ", ""))){
                        contactNumbers.add(phoneNumber.getMainData().replaceAll(" ", ""));
                    }
                }

                for (Email email : contactData.getEmailList()){
                    contactEmails.add(email.getMainData());
                }

                if (!contactData.getPhotoUri().equals(Uri.EMPTY)){
                    mContactPhotoStorageRef.child(contactName.trim()).putFile(contactData.getPhotoUri()).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    contactPhotoUrl[0] = uri.toString();
                                }
                            });
                        }
                    });
                }

                contactList.add(new ContactItem(contactName, contactEmails, contactPhotoUrl[0], contactNumbers));
                publishProgress("Getting Contacts ... " + "\n" + rawContactList.size() + "\\" + contactList.size());
            }

            return contactList;

        }



        ArrayList<MessageItem> initMessageList(){

            ArrayList<MessageItem> messageList = new ArrayList<>();

            Cursor messageCursor = getContentResolver().query(Telephony.Sms.CONTENT_URI, null, null, null, null);

            if (messageCursor != null) {
                while (messageCursor.moveToNext()){
                    EasyDeviceMod easyDeviceMod = new EasyDeviceMod(MainActivity.this);
                    int receiverSimId;
                    if (easyDeviceMod.getManufacturer().contains("xiaomi") || easyDeviceMod.getManufacturer().contains("Xiaomi")){
                        receiverSimId = Integer.parseInt(String.valueOf(messageCursor.getLong(messageCursor.getColumnIndex("sim_id"))));
                        if (receiverSimId > 2){ receiverSimId = 2; }
                    }else {
                        receiverSimId = Integer.parseInt(String.valueOf(messageCursor.getLong(messageCursor.getColumnIndex(Telephony.Sms.SUBSCRIPTION_ID))));
                    }
                    long messageId = messageCursor.getLong(messageCursor.getColumnIndex(Telephony.Sms.DATE))*-1;
                    String senderNumberOrDisplayName = messageCursor.getString(messageCursor.getColumnIndex(Telephony.Sms.ADDRESS));
                    String messageDate = dateFormatter(messageCursor.getLong(messageCursor.getColumnIndex(Telephony.Sms.DATE)));
                    String messageText = messageCursor.getString(messageCursor.getColumnIndex(Telephony.Sms.BODY));
                    String sentFrom = "phone";
                    String messageType = "";
                    MessagePersonItem messagePerson = null;
                    try {
                        messagePerson = new MessagePersonItem(senderNumberOrDisplayName, "", "", "", (senderNumberOrDisplayName!=null && Patterns.PHONE.matcher(senderNumberOrDisplayName).matches())?mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(senderNumberOrDisplayName, "GR"), Locale.getDefault()):"", (receiverSimId != 0)?mAllUserPhoneNumbers.get(receiverSimId-1):"", null);
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                    switch (messageCursor.getInt(messageCursor.getColumnIndex(Telephony.Sms.TYPE))){
                        case Telephony.Sms.MESSAGE_TYPE_INBOX:
                            messageType = "incoming";
                            break;
                        case Telephony.Sms.MESSAGE_TYPE_SENT:
                            messageType = "outgoing";
                            break;
                    }

                    if (senderNumberOrDisplayName != null && Patterns.PHONE.matcher(senderNumberOrDisplayName).matches()){
                        for (ContactItem contactItem : mInitialContactList){
                            if (contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(senderNumberOrDisplayName, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)) || contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(senderNumberOrDisplayName, PhoneNumberUtil.PhoneNumberFormat.E164))){
                                try {
                                    messagePerson = new MessagePersonItem(phoneNumberFormatter(senderNumberOrDisplayName, PhoneNumberUtil.PhoneNumberFormat.NATIONAL), contactItem.getmName(), (contactItem.getmAllContactEmails().size()>0)?contactItem.getmAllContactEmails().get(0):"", contactItem.getmPhotoUrl(), mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(senderNumberOrDisplayName, "GR"), Locale.getDefault()), (receiverSimId != 0)?mAllUserPhoneNumbers.get(receiverSimId-1):"", contactItem.getmAllContactNumbers());
                                } catch (NumberParseException e) {
                                    e.printStackTrace();
                                }
                                break;
                            }
                        }
                    }

                    if (senderNumberOrDisplayName != null){
                        messageList.add(new MessageItem(messageType, messageDate, messageText, sentFrom, messageId, messagePerson));
                        publishProgress("Getting Messages ... " + "\n" + messageCursor.getCount() + "\\" + messageList.size());
                    }
                }
            }

            return messageList;
        }



        ArrayList<CallItem> initCallList(){

            ArrayList<CallItem> callList = new ArrayList<>();

            @SuppressLint("MissingPermission") Cursor callLogCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);

            if (callLogCursor.getCount() > 0){
                while (callLogCursor.moveToNext()){
                    long callId = callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE))*-1;
                    String callNumber = callLogCursor.getString(callLogCursor.getColumnIndex(CallLog.Calls.NUMBER));
                    String callDuration = String.valueOf(callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DURATION)));
                    String callDate = dateFormatter(callLogCursor.getLong(callLogCursor.getColumnIndex(CallLog.Calls.DATE)));
                    String callType = "";
                    CallPersonItem callPerson = null;
                    try {
                        callPerson = new CallPersonItem(callNumber, "", "", "", mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(callNumber, "GR"), Locale.getDefault()), null);
                    } catch (NumberParseException e) {
                        e.printStackTrace();
                    }

                    switch (callLogCursor.getInt(callLogCursor.getColumnIndex(CallLog.Calls.TYPE))){
                        case CallLog.Calls.INCOMING_TYPE:
                            callType = "incoming";
                            break;
                        case CallLog.Calls.OUTGOING_TYPE:
                            callType = "outgoing";
                            break;
                        case CallLog.Calls.MISSED_TYPE:
                        case CallLog.Calls.REJECTED_TYPE:
                            callType = "missed";
                            break;
                    }

                    for (ContactItem contactItem : mInitialContactList){
                        if (contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(callNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL)) || contactItem.getmAllContactNumbers().contains(phoneNumberFormatter(callNumber, PhoneNumberUtil.PhoneNumberFormat.E164))){
                            try {
                                callPerson = new CallPersonItem(phoneNumberFormatter(callNumber, PhoneNumberUtil.PhoneNumberFormat.NATIONAL), contactItem.getmName(), (contactItem.getmAllContactEmails().size()>0)?contactItem.getmAllContactEmails().get(0):"", contactItem.getmPhotoUrl(), mGeocoder.getDescriptionForNumber(mPhoneUtil.parse(callNumber, "GR"), Locale.getDefault()), contactItem.getmAllContactNumbers());
                            } catch (NumberParseException e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    callList.add(new CallItem(callType, callDuration, callDate, callId, callPerson));
                    publishProgress("Getting Call logs ... " + "\n" + callLogCursor.getCount() + "\\" + callList.size());
                }
            }

            return callList;
        }

    }



}
