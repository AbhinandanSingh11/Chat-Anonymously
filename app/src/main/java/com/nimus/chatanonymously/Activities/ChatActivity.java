package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.nimus.chatanonymously.Adapter.ChatAdapter;
import com.nimus.chatanonymously.Model.Chat;
import com.nimus.chatanonymously.Model.LastMessage;
import com.nimus.chatanonymously.Model.User;
import com.nimus.chatanonymously.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;

public class ChatActivity extends AppCompatActivity {
    private AppCompatTextView name;
    private CircleImageView image;
    private EditText editText;
    private ImageView send;
    private String message;
    private FirebaseUser currentUser;
    private DatabaseReference reference,references;
    private ChatAdapter adapter;
    private ArrayList<Chat> chats;
    private RecyclerView recyclerView;
    private SharedPreferences sharedPreferences,firstSharedPrefrences;
    private ValueEventListener seenListener;
    private ProgressBar progressBar,progressBarImage;
    private ImageView emoji;
    private LinearLayout back;
    private JsonObjectRequest request;
    private RequestQueue queue;
    private boolean isAlreadyFirst = false;
    private final String URL = "https://fcm.googleapis.com/fcm/send";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ChatActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.pink,getTheme()));
        setContentView(R.layout.activity_chat);

        name = findViewById(R.id.nameChatWith);
        image = findViewById(R.id.imageChatWith);
        back = findViewById(R.id.LCD);
        send = findViewById(R.id.fabSendMessage);
        editText = findViewById(R.id.EditTextChat);
        recyclerView = findViewById(R.id.recyclerViewChatScreen);
        progressBar = findViewById(R.id.progressChatScreen);
        progressBarImage = findViewById(R.id.progressChatImage);
        emoji = findViewById(R.id.emoji);

        sharedPreferences = getPreferences(MODE_PRIVATE);


        firstSharedPrefrences = getPreferences(MODE_PRIVATE);

        queue = Volley.newRequestQueue(ChatActivity.this);


        final String id = getIntent().getStringExtra("id");
        final String firstBy = getIntent().getStringExtra("firstBy");
        final String options = getIntent().getStringExtra("options");



        setUpRecy();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = editText.getText().toString().trim();
                if(message.length()>0){

                    sendMessage(currentUser.getUid(),id,message,firstBy,options);
                    editText.setText(null);
                    addLastMessage(currentUser.getUid(),id,message);
                }
                else {
                    Toast.makeText(ChatActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }
            }
        });

        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String m = getEmojiByUnicode(0x2764);
                //String m = getEmojiByUnicode(0x1F642);
                sendMessage(currentUser.getUid(),id,m,firstBy,options);
                addLastMessage(currentUser.getUid(),id,m);
            }
        });


        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(id);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                User user = dataSnapshot.getValue(User.class);

                if(firstBy.equals("response")){
                    Glide.with(ChatActivity.this)
                            .load(R.drawable.pet)
                            .into(image);
                    name.setText("Anonymous user");
                    receiveMessage(currentUser.getUid(),id,"response");
                    progressBarImage.setVisibility(View.GONE);
                }
                else {
                    if(user != null) {

                        Glide.with(ChatActivity.this)
                                .load(user.getImage())
                                .into(image);
                        name.setText(user.getName());
                        receiveMessage(currentUser.getUid(),id,user.getImage());
                        progressBarImage.setVisibility(View.GONE);
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        seenMessage(id);
    }

    void setUpRecy(){
        recyclerView.hasFixedSize();
        LinearLayoutManager manager=  new LinearLayoutManager(ChatActivity.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
    }

    void sendMessage(final String sender, final String receiver, final String message, final String firstBy, final String options){


        final HashMap<String, Object> hashMap = new HashMap<>();
        final Gson gson = new Gson();
        DatabaseReference references = FirebaseDatabase.getInstance().getReference("Users");
        references.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("ApplySharedPref")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:  dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;
                    if(user.getId().equals(receiver)){

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        sharedPreferences.edit().remove("UserObject").commit();
                        editor.putString("UserObject",gson.toJson(user));
                        editor.commit();

                    }

                }

                String json = sharedPreferences.getString("UserObject","");
                DatabaseReference Reference;
                Reference = FirebaseDatabase.getInstance().getReference("Chats");
                hashMap.put("sender",sender);
                hashMap.put("receiver",receiver);
                hashMap.put("message",message);
                hashMap.put("isSeen",false);
                hashMap.put("userReceiver",gson.fromJson(json,User.class));
                hashMap.put("userSender",new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getDisplayName(),FirebaseAuth.getInstance().getCurrentUser().getEmail(),FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString()));

                if(firstBy.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                    hashMap.put("firstBy", currentUser.getUid());
                }
                if(firstBy.equals("response")){
                    hashMap.put("firstBy",receiver);
                }


                isAlreadyFirst = firstSharedPrefrences.getBoolean("isAlreadyFirst",false);

                if(!isAlreadyFirst && options.equals("first")){
                    hashMap.put("options","first");
                    SharedPreferences.Editor firstEditor = firstSharedPrefrences.edit();
                    firstEditor.putBoolean("isAlreadyFirst",true);
                    firstEditor.commit();
                }
                else if(isAlreadyFirst && options.equals("first")){
                    hashMap.put("options","repeat");
                }
                else{
                    hashMap.put("options",options);
                }

                //hashMap.put("options",options);
                Reference.push().setValue(hashMap);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    void seenMessage(final String userid){
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if(chat.getReceiver().equals(currentUser.getUid()) && chat.getSender().equals(userid)){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // This was added aafter commiting version 1.0.3

    void addPendingMessages(){

    }

    void addLastMessage(String sender, String receiver, String message){
        final HashMap<String, Object> hashMap = new HashMap<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("LastMessage");
        hashMap.put("sender", sender);
        hashMap.put("receiver",receiver);
        hashMap.put("lastMessage",message);

        ref.push().setValue(hashMap);
    }

    void receiveMessage(final String myid, final String userid, final String imgURL){
        progressBar.setVisibility(View.VISIBLE);
        chats = new ArrayList<>();
        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chats.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    assert chat != null;
                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) || chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        chats.add(chat);
                    }


                    adapter = new ChatAdapter(ChatActivity.this,chats,imgURL);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        reference.removeEventListener(seenListener);
    }

    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onDestroy() {
        super.onDestroy();
        firstSharedPrefrences.edit().remove("isAlreadyFirst").commit();
    }
}