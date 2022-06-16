package com.ossoft.personalmyinfocloud;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.ossoft.personalmyinfocloud.LoginActivity.RC_SIGN_IN;
import static com.ossoft.personalmyinfocloud.MainActivity.mAreAllPermissionsGranted;
import static com.ossoft.personalmyinfocloud.MainActivity.mProfilePhotoStorageRef;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserDatabaseRef;
import static com.ossoft.personalmyinfocloud.MainActivity.mUserInfoItem;

public class ProfileActivity extends AppCompatActivity implements OnMapReadyCallback {

    TextView mFullNameTxv, mUIDTxv, mFirstNameTxv, mLastNameTxv, mUserNumbersTxv, mUserEmailTxv, mLastLocationTxv;
    ImageView mBackToMain, mProfilePic;
    ImageButton mChangeProfilePicBtn;
    Button mEditInfoBtn;
    MapView mMapView;
    GoogleMap mGoogleMap;
    Geocoder mGeocoder;
    CustomProgressDialog mUploadProgressDialog;
    EditInfoDialog mEditInfoDialog;

    String WRITE_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    String READ_EXTERNAL_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE ) {
            if (resultCode == RESULT_OK) {
                Uri resultUri = CropImage.getPickImageResultUri(ProfileActivity.this ,data);
                startCrop(resultUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK){
                mUploadProgressDialog = new CustomProgressDialog(ProfileActivity.this, "Uploading ...");
                mUploadProgressDialog.setCanceledOnTouchOutside(false);
                mUploadProgressDialog.setCancelable(false);
                mUploadProgressDialog.show();

                Uri resultUri = result.getUri();
                uploadImage(resultUri);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Toasty.error(ProfileActivity.this, Objects.requireNonNull(result.getError().getMessage())).show();
            }
        }

        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            GoogleSignIn.getSignedInAccountFromIntent(data).addOnCompleteListener(new OnCompleteListener<GoogleSignInAccount>() {
                @Override
                public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                    if (task.isSuccessful()){
                        mEditInfoDialog.setNewGoogleEmail(task.getResult());
                    }else {
                        Toasty.error(ProfileActivity.this, task.getException().getMessage()).show();
                    }
                }
            });
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mGeocoder = new Geocoder(ProfileActivity.this);

        mFullNameTxv = findViewById(R.id.profile_full_name);
        mUIDTxv = findViewById(R.id.profile_UID);
        mFirstNameTxv = findViewById(R.id.profile_first_name);
        mLastNameTxv = findViewById(R.id.profile_last_name);
        mUserNumbersTxv = findViewById(R.id.profile_all_phone_numbers);
        mUserEmailTxv = findViewById(R.id.profile_all_emails);
        mLastLocationTxv = findViewById(R.id.profile_last_location);
        mBackToMain = findViewById(R.id.profile_back_to_main);
        mProfilePic = findViewById(R.id.profile_pic);
        mChangeProfilePicBtn = findViewById(R.id.profile_change_photo);
        mEditInfoBtn = findViewById(R.id.profile_edit_info);
        mMapView = findViewById(R.id.profile_map_view);

        mFullNameTxv.setText(mUserInfoItem.getmFirstName() + " " + mUserInfoItem.getmLastName());
        mUIDTxv.setText("UID: " + mUserInfoItem.getmUID());
        mFirstNameTxv.setText(mUserInfoItem.getmFirstName());
        mLastNameTxv.setText(mUserInfoItem.getmLastName());
        mUserEmailTxv.setText(mUserInfoItem.getmEmail());



        if (mUserInfoItem.getmAllSimNumbers() != null){
            String phoneNumbers = "";
            for (String number : mUserInfoItem.getmAllSimNumbers()){
                if (mUserInfoItem.getmAllSimNumbers().indexOf(number) == mUserInfoItem.getmAllSimNumbers().size()-1){
                    phoneNumbers = phoneNumbers.concat(number);
                }else {
                    phoneNumbers = phoneNumbers.concat(number).concat("\n");
                }
            }
            mUserNumbersTxv.setText(phoneNumbers);
        }



        try {
            if (mUserInfoItem.getmLastLocation() != null){
                List<Address> addressList = mGeocoder.getFromLocation(Double.valueOf(mUserInfoItem.getmLastLocation().get(0)), Double.valueOf(mUserInfoItem.getmLastLocation().get(1)), 1);
                if (addressList.size()>0){
                    Address address = addressList.get(0);
                    mLastLocationTxv.setText(address.getCountryName() + ", " + address.getLocality());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        
        Glide.with(ProfileActivity.this).load(mUserInfoItem.getmPhotoUrl()).placeholder(R.drawable.ic_account).fitCenter().into(mProfilePic);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);



        mBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finishAffinity();
            }
        });



        mChangeProfilePicBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.startPickImageActivity(ProfileActivity.this);
//                Dexter.withContext(ProfileActivity.this)
//                        .withPermissions(WRITE_EXTERNAL_STORAGE_PERMISSION, READ_EXTERNAL_STORAGE_PERMISSION)
//                        .withListener(new MultiplePermissionsListener() {
//                            @Override
//                            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
//                                if (multiplePermissionsReport.areAllPermissionsGranted()){
//                                    CropImage.activity()
//                                            .setGuidelines(CropImageView.Guidelines.ON)
//                                            .start(ProfileActivity.this);
//                                }else {
//                                    CropImage.startPickImageActivity(ProfileActivity.this);
//                                }
//                            }
//
//                            @Override
//                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
//
//                            }
//                        });
            }
        });



        mEditInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditInfoDialog = new EditInfoDialog(ProfileActivity.this, mUserInfoItem);
                mEditInfoDialog.setCanceledOnTouchOutside(false);
                mEditInfoDialog.setCancelable(false);
                mEditInfoDialog.show();
            }
        });

    }



    private void startCrop(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .setAspectRatio(1, 1)
                .start(ProfileActivity.this);
    }



    public String fileNameMaker (){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateAndTime = sdf.format(new Date());

        return currentDateAndTime;
    }



    private void uploadImage(Uri imageUri){

        mProfilePhotoStorageRef.child(fileNameMaker()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){

                    task.getResult().getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()){

                                mUserDatabaseRef.child("mUserInfo").child("mPhotoUrl").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            mUploadProgressDialog.cancel();
                                            Toasty.success(ProfileActivity.this, "Profile photo changed successfully").show();
                                            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                                            finishAffinity();

                                        }else {
                                            mUploadProgressDialog.cancel();
                                            Toasty.error(ProfileActivity.this, task.getException().getMessage()).show();
                                        }
                                    }
                                });

                            }else {
                                mUploadProgressDialog.cancel();
                                Toasty.error(ProfileActivity.this, task.getException().getMessage()).show();
                            }
                        }
                    });

                }else {
                    mUploadProgressDialog.cancel();
                    Toasty.error(ProfileActivity.this, task.getException().getMessage()).show();
                }
            }
        });

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        if (mAreAllPermissionsGranted){
            mGoogleMap.setMyLocationEnabled(true);
            if (mUserInfoItem.getmLastLocation() != null){
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.valueOf(mUserInfoItem.getmLastLocation().get(0)), Double.valueOf(mUserInfoItem.getmLastLocation().get(1)))));
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }

    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finishAffinity();
    }
}
