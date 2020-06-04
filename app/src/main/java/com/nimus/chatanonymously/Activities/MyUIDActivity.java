package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
                ClipData clipData = ClipData.newPlainText("MyUID",FirebaseAuth.getInstance().getCurrentUser().getUid());
                assert clipboardManager != null;
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(MyUIDActivity.this, "copied to clipboard", Toast.LENGTH_SHORT).show();


                String url = "https://askmeanonymously.page.link" +
                        "link=https://www.nimus.co.in" +
                        "&apn="+getPackageName() +
                        "&st="+"User ID"+
                        "&sd="+FirebaseAuth.getInstance().getCurrentUser().getUid();

                Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                        .setLongLink(Uri.parse(url))
                        .buildShortDynamicLink()
                        .addOnCompleteListener(new OnCompleteListener<ShortDynamicLink>() {
                            @Override
                            public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                                if(task.isSuccessful()){
                                    Uri shortLink = task.getResult().getShortLink();
                                    Uri flowchartLink = task.getResult().getPreviewLink();

                                    Log.e("Share my UID", "Generated Link: "+ shortLink.toString());
                                }

                                else{
                                    Log.e("Share my UID", Objects.requireNonNull(Objects.requireNonNull(task.getException()).getMessage()));
                                }
                            }
                        });

            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Ask me Anonymously");
                    String shareMessage = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch(Exception e) {
                    Log.d("Exception", "share Exception: "+e);
                }
            }
        });

        TextViewUID.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
    }
}
