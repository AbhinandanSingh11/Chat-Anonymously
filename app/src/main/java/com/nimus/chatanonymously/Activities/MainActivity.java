package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 99;
    private RecyclerView recyclerViewConversation, recyclerViewAsked;
    private AppCompatTextView name;
    private AppBarLayout appBarLayout;
    private CircleImageView imageView;
    private ConversationAdapter conversationAdapter;
    private AskedByAdapter askedByAdapter;
    private FloatingActionButton fab;
    private ArrayList<Chat> mConvo;
    private ArrayList<Chat> mChat;
    private FirebaseUser currentUser;
    private LinearLayout Noconvo, NoAskedByMe;
    private ProgressBar progressBar,progressBar2;
    private boolean doubleBackToExitPressedOnce=  false;
    private LinearLayout main, noInternet;
    private TextView myCovo, otherConvo;

    @Override
    public void onBackPressed() {
        if(doubleBackToExitPressedOnce){
            super.onBackPressed();
            finishAndRemoveTask();
            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        },2000);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        recyclerViewConversation = findViewById(R.id.recyclerView_recent);
        recyclerViewAsked = findViewById(R.id.recyclerview_new);
        fab = findViewById(R.id.fab_searchUser);
        name = findViewById(R.id.nameMainActivity);
        imageView = findViewById(R.id.ImageViewlogo);
        progressBar = findViewById(R.id.progressNewRecy);
        progressBar2 = findViewById(R.id.progressAskedByMeRecy);
        appBarLayout = findViewById(R.id.TopAppBar);
        Noconvo = findViewById(R.id.TextChats);
        NoAskedByMe = findViewById(R.id.TextAskedByYou);
        main = findViewById(R.id.L1);
        noInternet = findViewById(R.id.NoInternetMainActivity);
        myCovo = findViewById(R.id.headingNQVA);
        otherConvo = findViewById(R.id.headingRCVA);

        requestPermissions();


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        appBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);
            }
        });



        assert currentUser != null;
        readUsers(currentUser.getUid());
        setUpRecAskedByMe();
        setUpRec();

        name.setText("Hello "+currentUser.getDisplayName());
        Glide.with(MainActivity.this)
                .load(currentUser.getPhotoUrl())
                .placeholder(R.drawable.pet)
                .into(imageView);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ActivitySearchUser.class);
                startActivity(intent);
            }
        });

        myCovo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,MyConvoActivity.class);
                startActivity(intent);
                finish();
            }
        });

        otherConvo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ConversationActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    void setUpRec(){
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewConversation.setLayoutManager(manager);
        recyclerViewConversation.setItemAnimator(new DefaultItemAnimator());
        recyclerViewConversation.addItemDecoration(new DividerDecoration(this,DividerDecoration.VERTICAL_LIST,36));
    }

    void setUpRecAskedByMe(){
        LinearLayoutManager manager = new LinearLayoutManager(MainActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewAsked.setLayoutManager(manager);
        recyclerViewAsked.setItemAnimator(new DefaultItemAnimator());
        recyclerViewAsked.addItemDecoration(new DividerDecoration(this,DividerDecoration.VERTICAL_LIST,36));

    }


    private void readUsers(final String myID){
        progressBar2.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        mChat = new ArrayList<>();
        mConvo = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                mConvo.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    Chat chat = snapshot.getValue(Chat.class);

                    assert chat != null;


                    if(chat.getFirstBy().equals(myID) && chat.getSender().equals(myID)){
                        if(chat.getOptions().equals("first")){
                            mChat.add(chat);
                        }
                    }


                    if(chat.getReceiver().equals(myID) && !chat.getFirstBy().equals(myID)){
                        if(!chat.getOptions().equals("repeat")){
                            mConvo.add(chat);
                        }
                    }
                }

                if(mChat.size()==0){
                    NoAskedByMe.setVisibility(View.VISIBLE);
                }else {
                    NoAskedByMe.setVisibility(View.GONE);
                }

                if(mConvo.size()<=0){
                    Noconvo.setVisibility(View.VISIBLE);
                }else {
                    Noconvo.setVisibility(View.GONE);
                }

                askedByAdapter = new AskedByAdapter(getApplicationContext(), mChat);
                conversationAdapter = new ConversationAdapter(getApplicationContext(),mConvo);
                recyclerViewAsked.setAdapter(askedByAdapter);
                recyclerViewConversation.setAdapter(conversationAdapter);
                progressBar2.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressBar2.setVisibility(View.GONE);
            }
        });
    }


    void requestPermissions(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_NETWORK_STATE},REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ((requestCode)){
            case REQUEST_CODE:
                if(grantResults.length >0 && grantResults[0]  == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED){
                        Tovuti.from(MainActivity.this).monitor(new Monitor.ConnectivityListener() {
                            @Override
                            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                                if(!isConnected){
                                    setNoInternet();
                                }else {
                                    setInternet();
                                }
                            }
                        });
                    }
                }
                else{
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }
    }

    void setNoInternet(){
        main.setVisibility(View.GONE);
        noInternet.setVisibility(View.VISIBLE);
    }
    void setInternet(){
        main.setVisibility(View.VISIBLE);
        noInternet.setVisibility(View.GONE);
    }
}
