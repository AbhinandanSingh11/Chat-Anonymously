package com.nimus.chatanonymously.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nimus.chatanonymously.Model.Chat;
import com.nimus.chatanonymously.Model.LastMessage;
import com.nimus.chatanonymously.R;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private  static final int MSG_TYPE_LEFT = 0;
    private  static final int MSG_TYPE_RIGHT = 1;
    private Context context;
    private ArrayList<Chat> chats;
    private String imageURL;
    private FirebaseUser fUser;


    public ChatAdapter(Context context, ArrayList<Chat> chats, String imageURL) {
        this.context = context;
        this.chats = chats;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_RIGHT){
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false));
        }
        else{
            return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        Chat chat = chats.get(position);

        if(imageURL.equals("response")){
            Glide.with(context)
                    .load(R.drawable.pet)
                    .placeholder(R.drawable.pet)
                    .into(holder.profile_image);
        }
        else{
            Glide.with(context)
                    .load(imageURL)
                    .placeholder(R.drawable.pet)
                    .into(holder.profile_image);
        }


        holder.show_message.setText(chat.getMessage());


    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView show_message;
        private ImageView profile_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_message = itemView.findViewById(R.id.show_message);
            profile_image = itemView.findViewById(R.id.image_chat_item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(chats.get(position).getSender().equals(fUser.getUid())){
            return MSG_TYPE_RIGHT;
        }
        else{
            return MSG_TYPE_LEFT;
        }
    }
}
