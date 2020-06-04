package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nimus.chatanonymously.Adapter.AskedByAdapter;
import com.nimus.chatanonymously.Adapter.ConversationAdapter;
import com.nimus.chatanonymously.Custom.DividerDecoration;
import com.nimus.chatanonymously.Model.Chat;
import com.nimus.chatanonymously.R;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ConversationAdapter adapter;
    private ArrayList<Chat> mConvo;
    private ProgressBar progressBar;
    private LinearLayout layoutNo;
    private ImageView close;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ConversationActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_conversation);

        recyclerView = findViewById(R.id.RecyclerViewActivityConversation);
        progressBar = findViewById(R.id.progressActivityConversation);
        layoutNo = findViewById(R.id.TextChatsConvoActivity);
        close = findViewById(R.id.closeActivityConversation);

        readUsers(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setUpRec();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ConversationActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    void setUpRec(){
        LinearLayoutManager manager = new LinearLayoutManager(ConversationActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerDecoration(this,DividerDecoration.VERTICAL_LIST,36));
    }

    private void readUsers(final String myID){
        mConvo = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mConvo.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;

                    if(chat.getReceiver().equals(myID) && !chat.getFirstBy().equals(myID)){
                        if(!chat.getOptions().equals("repeat")){
                            mConvo.add(chat);
                        }
                    }
                }

                if(mConvo.size()==0){
                    layoutNo.setVisibility(View.VISIBLE);
                }else {
                    layoutNo.setVisibility(View.GONE);
                }


                adapter = new ConversationAdapter(getApplicationContext(),mConvo);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
