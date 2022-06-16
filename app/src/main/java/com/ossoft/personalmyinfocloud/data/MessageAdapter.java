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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private Context mContext;
    private ArrayList<MessageItem> mMessageList;

    public MessageAdapter(Context mContext, ArrayList<MessageItem> mMessageList) {
        this.mContext = mContext;
        this.mMessageList = mMessageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemRootView = LayoutInflater.from(mContext).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(itemRootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        holder.mRootItemView.setTag(position);
        holder.bind(mMessageList.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }









    class MessageViewHolder extends RecyclerView.ViewHolder{

        TextView mMessageNameTxv, mMessageTxtPreviewTxv, mMessageDateTxv;
        CircleImageView mMessageProfilePic;
        ImageView mMessageTypeImv;
        LinearLayout mRootItemView;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            mMessageNameTxv = itemView.findViewById(R.id.message_name);
            mMessageTxtPreviewTxv = itemView.findViewById(R.id.message_text_preview);
            mMessageDateTxv = itemView.findViewById(R.id.message_date);
            mMessageProfilePic = itemView.findViewById(R.id.message_profile_pic);
            mMessageTypeImv = itemView.findViewById(R.id.message_type_logo);
            mRootItemView = itemView.findViewById(R.id.message_item_root_view);
        }

        void bind(MessageItem messageItem){

            if (!messageItem.getmMessagePerson().getmContactName().equals("")){
                mMessageNameTxv.setText(messageItem.getmMessagePerson().getmContactName());
                mMessageTxtPreviewTxv.setText(messageItem.getmText());
                Glide.with(mContext).load(Uri.parse(messageItem.getmMessagePerson().getmContactPhoto())).placeholder(R.drawable.ic_account).fitCenter().into(mMessageProfilePic);
            }else {
                mMessageNameTxv.setText(messageItem.getmMessagePerson().getmMessageNumber());
                mMessageTxtPreviewTxv.setText(messageItem.getmText());
                mMessageProfilePic.setImageResource(R.drawable.ic_account);
            }

            switch (messageItem.getmType()){
                case "incoming":
                    mMessageTypeImv.setImageResource(R.drawable.ic_incoming);
                    break;
                case "outgoing":
                    mMessageTypeImv.setImageResource(R.drawable.ic_outgoing);
                    break;
                default:
                    mMessageTypeImv.setVisibility(View.GONE);
            }

            mMessageDateTxv.setText(messageItem.getmDate());

            mRootItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MessageItem clickedItem = mMessageList.get(((int) v.getTag()));
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

    }
}
