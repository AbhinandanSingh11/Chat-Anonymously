package com.nimus.chatanonymously.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nimus.chatanonymously.Activities.ChatActivity;
import com.nimus.chatanonymously.Model.Chat;
import com.nimus.chatanonymously.Model.LastMessage;
import com.nimus.chatanonymously.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AskedByAdapter extends RecyclerView.Adapter<AskedByAdapter.ViewHolder> {
    private Context context;
    private ArrayList<Chat> chats;

    public AskedByAdapter(Context context, ArrayList<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_asked_br_me,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("LastMessage");
        final ArrayList<String> last = new ArrayList<>();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                last.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    LastMessage lastMessage =  snapshot.getValue(LastMessage.class);
                    if(lastMessage.getSender().equals(chats.get(position).getSender()) && lastMessage.getReceiver().equals(chats.get(position).getReceiver())  || lastMessage.getSender().equals(chats.get(position).getReceiver())  && lastMessage.getReceiver().equals(chats.get(position).getSender())){
                        last.add(lastMessage.getLastMessage());
                    }
                }

                if(!last.isEmpty()){
                    Log.d("acmgajgakd",last.get(last.size()-1));
                    holder.message.setText(last.get(last.size()-1));
                }
                else{
                    holder.message.setText("No message");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        holder.name.setText(chats.get(position).getUserReceiver().getName());

        Glide.with(context)
                .load(chats.get(position).getUserReceiver().getImage())
                .placeholder(R.drawable.pet)
                .into(holder.image);
        //holder.message.setText(chats.get(position).getMessage());

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id", chats.get(position).getReceiver());
                intent.putExtra("firstBy", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("options","repeat");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView image;
        private ConstraintLayout container;
        private TextView name;
        private TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.item_name_asked_by__me);
            message = itemView.findViewById(R.id.item_message_asked_by_me);
            image = itemView.findViewById(R.id.item_image_asked_by_me);
            container = itemView.findViewById(R.id.container_asked_by_me);
        }
    }
}
