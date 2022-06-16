package com.ossoft.personalmyinfocloud;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.ossoft.personalmyinfocloud.MainActivity.mCallAdapter;


public class CallFragment extends Fragment {

    private View mRootView;
    private RecyclerView mCallRecyclerView;

    public CallFragment() { }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP_MR1)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_call, container, false);
        mCallRecyclerView = mRootView.findViewById(R.id.call_recycler_view);

        mCallRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mCallRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        mCallRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mCallRecyclerView.setAdapter(mCallAdapter);
        mCallAdapter.notifyDataSetChanged();

        return mRootView;
    }


}
