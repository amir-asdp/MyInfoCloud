package com.ossoft.personalmyinfocloud.data;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ossoft.personalmyinfocloud.ItemInfoDialog;
import com.ossoft.personalmyinfocloud.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchItemAdapter extends RecyclerView.Adapter<SearchItemAdapter.SearchViewHolder> {

    private Context mContext;
    private ArrayList<Object> mSearchItemList;

    public SearchItemAdapter(Context mContext, ArrayList<Object> mSearchItemList) {
        this.mContext = mContext;
        this.mSearchItemList = mSearchItemList;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRootView = LayoutInflater.from(mContext).inflate(R.layout.item_search, parent, false);
        return new SearchViewHolder(itemRootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        holder.mSearchItemRootView.setTag(position);
        switch (mSearchItemList.get(position).getClass().getSimpleName()){

            case "CallItem":
                holder.bindCallItem(((CallItem) mSearchItemList.get(position)));
                break;

            case "MessageItem":
                holder.bindMessageItem(((MessageItem) mSearchItemList.get(position)));
                break;

            case "NoteItem":
                holder.bindNoteItem(((NoteItem) mSearchItemList.get(position)));
                break;

        }
    }

    @Override
    public int getItemCount() {
        return mSearchItemList.size();
    }










    class SearchViewHolder extends RecyclerView.ViewHolder{

        TextView mSearchItemTitleTxv, mSearchItemDescriptionTxv, mSearchItemDateTxv;
        CircleImageView mSearchItemPic;
        ImageView mSearchItemTypeImv;
        LinearLayout mSearchItemRootView;

        SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            mSearchItemTitleTxv = itemView.findViewById(R.id.search_item_title);
            mSearchItemDescriptionTxv = itemView.findViewById(R.id.search_item_description);
            mSearchItemDateTxv = itemView.findViewById(R.id.search_item_date);
            mSearchItemPic = itemView.findViewById(R.id.search_item_pic);
            mSearchItemTypeImv = itemView.findViewById(R.id.search_item_type_logo);
            mSearchItemRootView = itemView.findViewById(R.id.search_item_root_view);
        }

        void bindCallItem(CallItem callItem){

            if (!callItem.getmCallPerson().getmContactName().equals("")){
                mSearchItemTitleTxv.setText(callItem.getmCallPerson().getmContactName());
                mSearchItemDescriptionTxv.setText(callItem.getmCallPerson().getmCallNumber());
                Glide.with(mContext).load(Uri.parse(callItem.getmCallPerson().getmContactPhoto())).placeholder(R.drawable.ic_account).fitCenter().into(mSearchItemPic);
            }else {
                mSearchItemTitleTxv.setText(callItem.getmCallPerson().getmCallNumber());
                mSearchItemDescriptionTxv.setText(callItem.getmCallPerson().getmCallNumber());
                mSearchItemPic.setImageResource(R.drawable.ic_account);
            }

            switch (callItem.getmType()){
                case "incoming":
                    mSearchItemTypeImv.setImageResource(R.drawable.ic_incoming);
                    break;
                case "outgoing":
                    mSearchItemTypeImv.setImageResource(R.drawable.ic_outgoing);
                    break;
                case "missed":
                    mSearchItemTypeImv.setImageResource(R.drawable.ic_missed);
                    break;
                default:
                    mSearchItemTypeImv.setVisibility(View.GONE);
            }

            mSearchItemDateTxv.setText(callItem.getmDate());

            mSearchItemRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallItem clickedItem = ((CallItem) mSearchItemList.get(((int) v.getTag())));
                    String otherCallNumbers = "";
                    if (clickedItem.getmCallPerson().getmAllContactNumbers() != null){
                        for (String number : clickedItem.getmCallPerson().getmAllContactNumbers()){
                            if (!number.equals(clickedItem.getmCallPerson().getmCallNumber())){
                                if (otherCallNumbers.equals("")){
                                    otherCallNumbers = otherCallNumbers.concat(number);
                                }else {
                                    otherCallNumbers = otherCallNumbers.concat("\n").concat(number);
                                }
                            }
                        }
                    }
                    ItemInfoDialog callInfoDialog = new ItemInfoDialog(mContext, clickedItem.getmCallPerson().getmContactPhoto(), clickedItem.getmCallPerson().getmContactName(), clickedItem.getmCallPerson().getmCountry(), clickedItem.getmCallDuration(), clickedItem.getmType(), clickedItem.getmDate(), String.valueOf(clickedItem.getmCallId()), clickedItem.getmCallPerson().getmContactEmail(), clickedItem.getmCallPerson().getmCallNumber(), otherCallNumbers);
                    callInfoDialog.show();
                }
            });

        }

        void bindMessageItem(MessageItem messageItem){

            if (!messageItem.getmMessagePerson().getmContactName().equals("")){
                mSearchItemTitleTxv.setText(messageItem.getmMessagePerson().getmContactName());
                mSearchItemDescriptionTxv.setText(messageItem.getmText());
                Glide.with(mContext).load(Uri.parse(messageItem.getmMessagePerson().getmContactPhoto())).placeholder(R.drawable.ic_account).fitCenter().into(mSearchItemPic);
            }else {
                mSearchItemTitleTxv.setText(messageItem.getmMessagePerson().getmMessageNumber());
                mSearchItemDescriptionTxv.setText(messageItem.getmText());
                mSearchItemPic.setImageResource(R.drawable.ic_account);
            }

            switch (messageItem.getmType()){
                case "incoming":
                    mSearchItemTypeImv.setImageResource(R.drawable.ic_incoming);
                    break;
                case "outgoing":
                    mSearchItemTypeImv.setImageResource(R.drawable.ic_outgoing);
                    break;
                default:
                    mSearchItemTypeImv.setVisibility(View.GONE);
            }

            mSearchItemDateTxv.setText(messageItem.getmDate());
            mSearchItemDescriptionTxv.setMaxLines(1);

            mSearchItemRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageItem clickedItem = ((MessageItem) mSearchItemList.get(((int) v.getTag())));
                    String otherMessageNumbers = "";
                    if (clickedItem.getmMessagePerson().getmAllContactNumbers() != null){
                        for (String number : clickedItem.getmMessagePerson().getmAllContactNumbers()){
                            if (!number.equals(clickedItem.getmMessagePerson().getmMessageNumber())){
                                if (otherMessageNumbers.equals("")){
                                    otherMessageNumbers = otherMessageNumbers.concat(number);
                                }else {
                                    otherMessageNumbers = otherMessageNumbers.concat("\n").concat(number);
                                }
                            }
                        }
                    }
                    ItemInfoDialog messageInfoDialog = new ItemInfoDialog(mContext, clickedItem.getmMessagePerson().getmContactPhoto(), clickedItem.getmMessagePerson().getmContactName(), clickedItem.getmMessagePerson().getmCountry(), clickedItem.getmSentFrom(), clickedItem.getmType(), clickedItem.getmDate(), String.valueOf(clickedItem.getmMessageId()), clickedItem.getmMessagePerson().getmContactEmail(), clickedItem.getmMessagePerson().getmReceiverPhoneNum(), clickedItem.getmMessagePerson().getmMessageNumber(), otherMessageNumbers, clickedItem.getmText());
                    messageInfoDialog.show();
                }
            });

        }

        void bindNoteItem(NoteItem noteItem){

            mSearchItemTitleTxv.setText(noteItem.getmSubject());
            mSearchItemDescriptionTxv.setText(noteItem.getmText());
            mSearchItemDateTxv.setText(noteItem.getmDate());
            mSearchItemPic.setImageResource(R.drawable.ic_note_item);
            mSearchItemTypeImv.setVisibility(View.GONE);

            mSearchItemRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NoteItem clickedItem = ((NoteItem) mSearchItemList.get(((int) v.getTag())));
                    ItemInfoDialog noteInfoDialog = new ItemInfoDialog(mContext, clickedItem.getmSubject(), clickedItem.getmText(), clickedItem.getmNoteId());
                    noteInfoDialog.show();
                }
            });

        }

    }
}
