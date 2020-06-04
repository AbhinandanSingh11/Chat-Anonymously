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

public class MyConvoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AskedByAdapter adapter;
    private ArrayList<Chat> mConvo;
    private ProgressBar progressBar;
    private LinearLayout layoutNo;
    private ImageView close;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyConvoActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_convo);

        recyclerView = findViewById(R.id.RecyclerViewActivityMyConvo);
        progressBar = findViewById(R.id.progressActivityMyConversation);
        layoutNo = findViewById(R.id.TextChatsMyConvoActivity);
        close = findViewById(R.id.closeActivityMyConversation);

        readUsers(FirebaseAuth.getInstance().getCurrentUser().getUid());
        setUpRec();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyConvoActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void setUpRec(){
        LinearLayoutManager manager = new LinearLayoutManager(MyConvoActivity.this);
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

                    if(chat.getFirstBy().equals(myID) && chat.getSender().equals(myID)){
                        if(chat.getOptions().equals("first")){
                            mConvo.add(chat);
                        }
                    }
                }

                if(mConvo.size()==0){
                    layoutNo.setVisibility(View.VISIBLE);
                }else {
                    layoutNo.setVisibility(View.GONE);
                }


                adapter = new AskedByAdapter(getApplicationContext(),mConvo);
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
