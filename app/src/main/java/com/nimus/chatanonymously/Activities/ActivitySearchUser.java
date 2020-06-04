package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.nimus.chatanonymously.Adapter.UserAdapter;
import com.nimus.chatanonymously.Model.User;
import com.nimus.chatanonymously.R;

import java.util.ArrayList;

public class ActivitySearchUser extends AppCompatActivity {
    private EditText editText;
    private ImageView close;
    private FloatingActionButton fab;
    private RecyclerView recyclerView;
    private TextView errorMessage;
    private ProgressBar progressBar;
    private UserAdapter adapter;
    private String query;
    private ArrayList<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_search_user);

        editText = findViewById(R.id.getusers);
        fab = findViewById(R.id.fabSearchUser);
        recyclerView = findViewById(R.id.recyclerviewSearchUser);
        progressBar = findViewById(R.id.progressSearch);
        errorMessage = findViewById(R.id.errorSearch);
        close = findViewById(R.id.closeSearchUser);

        getDeepLinks();


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivitySearchUser.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = editText.getText().toString().trim();

                if(query.length()>0){
                    new Sync().execute(query);
                }

                else{
                    Toast.makeText(ActivitySearchUser.this, "Invalid User ID", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void getUser(final String id){

        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert mUser != null;
                    assert user != null;
                    if(user.getId().equals(id) && !user.getId().equals(mUser.getUid())){
                        users.add(user);
                    }


                }

                if(users.size()<=0){
                    errorMessage.setVisibility(View.VISIBLE);
                }
                else{
                    errorMessage.setVisibility(View.GONE);
                }

                adapter = new UserAdapter(ActivitySearchUser.this,users);
                LinearLayoutManager manager = new LinearLayoutManager(ActivitySearchUser.this);
                manager.setOrientation(RecyclerView.VERTICAL);
                recyclerView.setLayoutManager(manager);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ActivitySearchUser.this, "Error: " + databaseError, Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    class Sync extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... strings) {
            getUser(strings[0]);
            return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDeepLinks();
    }

    void getDeepLinks(){
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent()).addOnSuccessListener(new OnSuccessListener<PendingDynamicLinkData>() {
            @Override
            public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                Log.d("Firebase Dynamic Links","Link Found");

                Uri deepLink = null;
                if(pendingDynamicLinkData != null){
                    deepLink = pendingDynamicLinkData.getLink();
                }
                if(deepLink != null){
                    Log.d("Firebase Dynamic Links","Deep Link Found");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Firebase Dynamic Links", "Failed to Get link: "+ e.getLocalizedMessage());
            }
        });
    }

}
