package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.nimus.chatanonymously.Activities.ProfileActivity;
import com.nimus.chatanonymously.R;

import java.util.Objects;

public class MyUIDActivity extends AppCompatActivity {
    private ImageView close;
    private FloatingActionButton copy,share;
    private TextView TextViewUID;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MyUIDActivity.this,ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_my_u_i_d);


        close = findViewById(R.id.closeMyUID);
        copy = findViewById(R.id.fabCopyUID);
        share = findViewById(R.id.fabShareUID);
        TextViewUID = findViewById(R.id.textMyUID);
        progressBar = findViewById(R.id.ProgressMyUidActivity);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyUIDActivity.this, ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                new Sync2().execute(clipboardManager);

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Sync().execute();
            }
        });

        TextViewUID.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }

    void generate(final ClipboardManager clipboardManager){
        String url = "https://askmeanonymously.page.link/?" +
                "link=https://www.nimus.co.in/user?" +FirebaseAuth.getInstance().getCurrentUser().getUid()+
                "&apn="+getPackageName()+
                "&afl="+"https://www.nimus.co.in/download-ama";


        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLongLink(Uri.parse(url))
                .buildShortDynamicLink()
                .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if(task.isSuccessful()){
                            Uri shortLink = task.getResult().getShortLink();
                            Uri flowchartLink = task.getResult().getPreviewLink();

                            ClipData clipData = ClipData.newPlainText("MyUID",shortLink.toString());
                            assert clipboardManager != null;
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(MyUIDActivity.this, "copied to clipboard", Toast.LENGTH_SHORT).show();
                            Log.e("Share my UID", "Generated Link: "+ shortLink.toString());
                            progressBar.setVisibility(View.GONE);


                        }

                        else{
                            Log.e("Share my UID", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });



    }

    void generate(){

        String url = "https://askmeanonymously.page.link/?" +
                "link=https://www.nimus.co.in/user?" +FirebaseAuth.getInstance().getCurrentUser().getUid()+
                "&apn="+getPackageName()+
                "&st="+ "Ask me Anonymously"+
                "&sd="+ "Ask me questions or confess anything, without me knowing who you are...."+
                "&afl="+"https://www.nimus.co.in/download-ama"+
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
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ask me Anonymously");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                                startActivity(Intent.createChooser(shareIntent, "choose one"));
                                progressBar.setVisibility(View.GONE);
                            } catch(Exception e) {
                                Log.d("Exception", "share Exception: "+e);
                                progressBar.setVisibility(View.GONE);
                            }

                        }

                        else{
                            Log.e("Share my UID", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }

    class Sync extends AsyncTask<Void, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            generate();
            return null;
        }
    }

    class Sync2 extends AsyncTask<ClipboardManager, Void, Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(ClipboardManager... clipboardManagers) {
            generate(clipboardManagers[0]);
            return null;
        }
    }
}
