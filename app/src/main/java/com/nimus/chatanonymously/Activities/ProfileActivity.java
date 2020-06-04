package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nimus.chatanonymously.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    private CircleImageView image;
    private ImageView back;
    private TextView name,email;
    private FirebaseUser mUser;
    private LinearLayout logout,myUID,share,privacy;
    private FirebaseAuth.AuthStateListener authStateListener;

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
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ask me Anonymously");
                    String shareMessage= "\nDownload this app to chat with anyone or confess to anyone without revealing who you are\n\n";
                    shareMessage = shareMessage + "https://wwww.google.com";
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    Log.d("Exception", "share Exception: "+e);
                }
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
                intent.putExtra("code",0);
                intent.putExtra("URL","https://www.nimus.co.in/privacy-policy-ama");
                startActivity(intent);
            }
        });
    }

}
