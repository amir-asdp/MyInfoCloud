package com.ossoft.personalmyinfocloud;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class CustomProgressDialog extends Dialog {

    private String mProgressText;
    private TextView mProgressTxv;
    private ProgressBar mProgressBar;

    void setProgressText(String progressText) {
        this.mProgressText = progressText;
        this.mProgressTxv.setText(progressText);
    }

    CustomProgressDialog(@NonNull Context context, String progressText) {
        super(context);
        mProgressText = progressText;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_custom_progress);
        mProgressTxv = findViewById(R.id.progress_txt_dialog);
        mProgressBar = findViewById(R.id.progress_bar_dialog);
        mProgressTxv.setText(mProgressText);
    }



}
