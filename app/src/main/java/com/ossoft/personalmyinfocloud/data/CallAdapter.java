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

public class CallAdapter extends RecyclerView.Adapter<CallAdapter.CallViewHolder> {

    private Context mContext;
    private ArrayList<CallItem> mCallList;

    public CallAdapter(Context mContext, ArrayList<CallItem> mCallList) {
        this.mContext = mContext;
        this.mCallList = mCallList;
    }

    @NonNull
    @Override
    public CallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRootView = LayoutInflater.from(mContext).inflate(R.layout.item_call, parent, false);
        return new CallViewHolder(itemRootView);
    }

    @Override
    public void onBindViewHolder(@NonNull CallViewHolder holder, int position) {
        holder.mRootItemView.setTag(position);
        holder.bind(mCallList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCallList.size();
    }









    class CallViewHolder extends RecyclerView.ViewHolder{

        TextView mCallerNameTxv, mCallerNumberTxv, mCallDateTxv;
        CircleImageView mCallerProfilePic;
        ImageView mCallTypeImv;
        LinearLayout mRootItemView;

        CallViewHolder(@NonNull View itemView) {
            super(itemView);
            mCallerNameTxv = itemView.findViewById(R.id.call_item_name);
            mCallerNumberTxv = itemView.findViewById(R.id.call_item_number);
            mCallDateTxv = itemView.findViewById(R.id.call_item_date);
            mCallerProfilePic = itemView.findViewById(R.id.call_item_profile_pic);
            mCallTypeImv = itemView.findViewById(R.id.call_item_type_logo);
            mRootItemView = itemView.findViewById(R.id.call_item_root_view);
        }

        void bind(CallItem callItem){

            if (!callItem.getmCallPerson().getmContactName().equals("")){
                mCallerNameTxv.setText(callItem.getmCallPerson().getmContactName());
                mCallerNumberTxv.setText(callItem.getmCallPerson().getmCallNumber());
                Glide.with(mContext).load(Uri.parse(callItem.getmCallPerson().getmContactPhoto())).placeholder(R.drawable.ic_account).fitCenter().into(mCallerProfilePic);
            }else {
                mCallerNameTxv.setText(callItem.getmCallPerson().getmCallNumber());
                mCallerNumberTxv.setText(callItem.getmCallPerson().getmCallNumber());
                mCallerProfilePic.setImageResource(R.drawable.ic_account);
            }

            switch (callItem.getmType()){
                case "incoming":
                    mCallTypeImv.setImageResource(R.drawable.ic_incoming);
                    break;
                case "outgoing":
                    mCallTypeImv.setImageResource(R.drawable.ic_outgoing);
                    break;
                case "missed":
                    mCallTypeImv.setImageResource(R.drawable.ic_missed);
                    break;
                default:
                    mCallTypeImv.setVisibility(View.GONE);
            }

            mCallDateTxv.setText(callItem.getmDate());

            mRootItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CallItem clickedItem = mCallList.get(((int) v.getTag()));
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

    }

}
