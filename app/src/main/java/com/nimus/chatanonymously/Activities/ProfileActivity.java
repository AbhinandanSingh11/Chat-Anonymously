package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.nimus.chatanonymously.R;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView image;
    private ImageView back;
    private TextView name,email;
    private FirebaseUser mUser;
    private LinearLayout logout,myUID,share,privacy,update,feedback;
    private ImageView shareImage;
    private ProgressBar progressBarShareImage;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_profile);



        image = findViewById(R.id.imageUserProfile);
        name = findViewById(R.id.nameProfile);
        email = findViewById(R.id.emailProfile);
        logout = findViewById(R.id.layoutLogout);
        back = findViewById(R.id.backProfile);
        myUID = findViewById(R.id.layoutUID);
        share = findViewById(R.id.layoutShare);
        privacy = findViewById(R.id.layoutPrivacy);
        shareImage = findViewById(R.id.imageShare);
        progressBarShareImage = findViewById(R.id.progressShareApp);
        update = findViewById(R.id.layoutUpdate);
        feedback = findViewById(R.id.layoutFeedback);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        Glide.with(ProfileActivity.this)
                .load(mUser.getPhotoUrl())
                .placeholder(R.drawable.pet)
                .into(image);

        name.setText(mUser.getDisplayName());
        email.setText(mUser.getEmail());

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generate();
            }
        });

        myUID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MyUIDActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser != null){
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(ProfileActivity.this,ActivityLogin.class);
                    startActivity(intent);
                }
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Browser.class);
                intent.putExtra("URL","https://www.nimus.co.in/privacy-policy-ama");
                startActivity(intent);
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Browser.class);
                intent.putExtra("URL","https://www.nimus.co.in/download-ama");
                startActivity(intent);
            }
        });

        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, Browser.class);
                intent.putExtra("URL","https://www.nimus.co.in/feedback-ama");
                startActivity(intent);
            }
        });


    }
    void generate(){

        shareImage.setVisibility(View.GONE);
        progressBarShareImage.setVisibility(View.VISIBLE);

        String url = "https://askmeanonymously.page.link/?" +
                "link=https://www.nimus.co.in/share?" +
                "&apn="+getPackageName() +
                "&st="+ "Ask me Anonymously" +
                "&sd="+ "Chat with friends or confess anything, without them knowing who are you." +
                "&afl="+"https://www.nimus.co.in/download-ana"+
                "&si="+"https://www.api.nimus.co.in/images/pet.png";

        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(url))
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if(task.isSuccessful()){
                            Uri shortLink = task.getResult().getShortLink();

                            try {
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                //shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ask me Anonymously");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                                shareImage.setVisibility(View.VISIBLE);
                                progressBarShareImage.setVisibility(View.GONE);

                            } catch(Exception e) {
                                Log.d("Exception", "share Exception: "+e);
                                shareImage.setVisibility(View.VISIBLE);
                                progressBarShareImage.setVisibility(View.GONE);
                            }

                        }

                        else{
                            Log.e("Share my UID", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                        }
                    }
                });

    }
}
