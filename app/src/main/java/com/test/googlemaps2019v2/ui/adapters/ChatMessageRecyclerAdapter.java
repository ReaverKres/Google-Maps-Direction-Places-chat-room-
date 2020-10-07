package com.test.googlemaps2019v2.ui.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.test.googlemaps2019v2.R;
import com.test.googlemaps2019v2.models.chat.ChatMessage;
import com.test.googlemaps2019v2.models.user.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatMessageRecyclerAdapter extends RecyclerView.Adapter<ChatMessageRecyclerAdapter.ViewHolder>{

    private ArrayList<ChatMessage> mMessages = new ArrayList<>();
    private ArrayList<User> mUsers = new ArrayList<>();
    private Context mContext;
    private static final String TAG = "ChatMessageAdapter";

    public ChatMessageRecyclerAdapter(ArrayList<ChatMessage> messages,
                                      ArrayList<User> users,
                                      Context context) {
        this.mMessages = messages;
        this.mUsers = users;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chat_message_list_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        if(FirebaseAuth.getInstance().getUid().equals(mMessages.get(position).getUser().getUser_id())){
            (holder).username.setTextColor(ContextCompat.getColor(mContext, R.color.green1));
        }
        else{
            (holder).username.setTextColor(ContextCompat.getColor(mContext, R.color.blue2));
        }

        (holder).username.setText(mMessages.get(position).getUser().getUsername());
        (holder).message.setText(mMessages.get(position).getMessage());


        RequestOptions requestOptions = new RequestOptions()
                .error(R.drawable.cwm_logo)
                .placeholder(R.drawable.cwm_logo);

        int avatar = 0;
        try{
            avatar = Integer.parseInt(mMessages.get(position).getUser().getAvatar());
        }catch (NumberFormatException e){
            Log.e(TAG, "retrieveProfileImage: no avatar image. Setting default. " + e.getMessage() );
        }

        Glide.with(mContext)
                .setDefaultRequestOptions(requestOptions)
                .load(avatar)
                .into((holder).userAvatar);

        Log.d(TAG, "onBindViewHolder: Last " + mMessages.get(position).getUser().getAvatar());

    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView message, username;
        ImageView userAvatar;

        public ViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.chat_message_message);
            username = itemView.findViewById(R.id.chat_message_username);
            userAvatar = itemView.findViewById(R.id.chat_message_avatar);
        }
    }

    private void retrieveProfileImage(){
//        RequestOptions requestOptions = new RequestOptions()
//                .error(R.drawable.cwm_logo)
//                .placeholder(R.drawable.cwm_logo);
//
//        int avatar = 0;
//        try{
//            avatar = Integer.parseInt(((UserClient)mContext).getUser().getAvatar());
//        }catch (NumberFormatException e){
//            Log.e(TAG, "retrieveProfileImage: no avatar image. Setting default. " + e.getMessage() );
//        }
//
//        Glide.with(mContext)
//                .setDefaultRequestOptions(requestOptions)
//                .load(avatar)
//                .into((holder).userAvatar);
    }
}
















