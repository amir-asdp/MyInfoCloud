package com.ossoft.personalmyinfocloud;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class StartupActivity extends AppCompatActivity {

    public static SharedPreferences mIsLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);



        if (mIsLogin == null){
            mIsLogin = getSharedPreferences("is_login", MODE_PRIVATE);
        }



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsLogin.getString("is_login", "false").equals("true")){
                    startActivity(new Intent(StartupActivity.this, MainActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(StartupActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }, 2000);

    }



}
