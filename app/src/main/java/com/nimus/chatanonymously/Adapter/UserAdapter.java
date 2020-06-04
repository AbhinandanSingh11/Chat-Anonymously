package com.nimus.chatanonymously.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.nimus.chatanonymously.Activities.ChatActivity;
import com.nimus.chatanonymously.Model.User;
import com.nimus.chatanonymously.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private ArrayList<User> users;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_user,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final User user = users.get(position);
        holder.name.setText(user.getName());
        Glide.with(context)
                .load(user.getImage())
                .placeholder(R.drawable.pet)
                .into(holder.image);
        holder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("id",user.getId());
                intent.putExtra("firstBy", FirebaseAuth.getInstance().getCurrentUser().getUid());
                intent.putExtra("options","first");
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView image;
        private TextView name;
        private ConstraintLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.item_user_image);
            name = itemView.findViewById(R.id.item_name);
            root = itemView.findViewById(R.id.rootUserItem);
        }
    }
}
