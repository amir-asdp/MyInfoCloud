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
import android.widget.ImageView;

import static com.ossoft.personalmyinfocloud.MainActivity.mBaseNoteList;
import static com.ossoft.personalmyinfocloud.MainActivity.mNoteAdapter;


public class NoteFragment extends Fragment {

    private View mRootView;
    private ImageView mNoteEmptyStateLogo;
    private RecyclerView mNoteRecyclerView;

    public NoteFragment() { }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_note, container, false);
        mNoteEmptyStateLogo = mRootView.findViewById(R.id.note_empty_state_logo);
        mNoteRecyclerView = mRootView.findViewById(R.id.note_recycler_view);

        if (mBaseNoteList.size() > 0){
            mNoteEmptyStateLogo.setVisibility(View.GONE);
        }else {
            mNoteEmptyStateLogo.setVisibility(View.VISIBLE);
        }

        mNoteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mNoteRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        mNoteRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNoteRecyclerView.setAdapter(mNoteAdapter);
        mNoteAdapter.notifyDataSetChanged();

        return mRootView;
    }

}
