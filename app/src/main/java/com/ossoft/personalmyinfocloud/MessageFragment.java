package com.ossoft.personalmyinfocloud;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static com.ossoft.personalmyinfocloud.MainActivity.mMessageAdapter;


public class MessageFragment extends Fragment {

    private View mRootView;
    private RecyclerView mMessageRecyclerView;

    public MessageFragment() { }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_message, container, false);
        mMessageRecyclerView = mRootView.findViewById(R.id.message_recycler_view);

        mMessageRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mMessageRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        mMessageRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mMessageRecyclerView.setAdapter(mMessageAdapter);
        mMessageAdapter.notifyDataSetChanged();

        return mRootView;
    }


}
