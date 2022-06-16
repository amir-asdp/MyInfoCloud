package com.ossoft.personalmyinfocloud.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;
import com.ossoft.personalmyinfocloud.ItemInfoDialog;
import com.ossoft.personalmyinfocloud.R;

import java.util.ArrayList;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private Context mContext;
    private ArrayList<NoteItem> mNoteList;

    public NoteAdapter(Context mContext, ArrayList<NoteItem> mNoteList) {
        this.mContext = mContext;
        this.mNoteList = mNoteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRootView = LayoutInflater.from(mContext).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemRootView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.mRootItemView.setTag(position);
        holder.bind(mNoteList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }









    class NoteViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView mNoteTitleTxv, mNoteTextPreviewTxv, mNoteDate;
        LinearLayout mRootItemView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            mNoteTitleTxv = itemView.findViewById(R.id.note_title);
            mNoteTextPreviewTxv = itemView.findViewById(R.id.note_text_preview);
            mNoteDate = itemView.findViewById(R.id.note_date);
            mRootItemView = itemView.findViewById(R.id.note_item_root_view);
        }

        void bind(NoteItem noteItem){

            mNoteTitleTxv.setText(noteItem.getmSubject());
            mNoteTextPreviewTxv.setText(noteItem.getmText());
            mNoteDate.setText(noteItem.getmDate());

            mRootItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteItem clickedItem = mNoteList.get(((int) v.getTag()));
                    ItemInfoDialog noteInfoDialog = new ItemInfoDialog(mContext, clickedItem.getmSubject(), clickedItem.getmText(), clickedItem.getmNoteId());
                    noteInfoDialog.show();
                }
            });

        }

    }

}
