package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.nimus.chatanonymously.R;

public class SplashScreen extends AppCompatActivity {

    private String receivedUserID = null;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAndRemoveTask();
        finishAffinity();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);




        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                new Sync().execute();
            }
        },3000);
    }

    void getPendingLinks(){
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        Uri deepLink = null;
                        if(pendingDynamicLinkData != null){
                            deepLink = pendingDynamicLinkData.getLink();
                            Log.e("Splash Dynamic Link","Link found: "+  deepLink.toString());
                            //Toast.makeText(SplashScreen.this, "Splash Link found", Toast.LENGTH_SHORT).show();



                            receivedUserID = deepLink.toString().substring(deepLink.toString().indexOf("?")+1);
                            Log.e("Login Dynamic Link","USER ID: "+ receivedUserID);
                            mAuth = FirebaseAuth.getInstance();
                            user = mAuth.getCurrentUser();

                            if(user!=null){
                                Intent intent = new Intent(SplashScreen.this,ActivitySearchUser.class);
                                intent.putExtra("receivedUID",receivedUserID);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Intent intent = new Intent(SplashScreen.this,ActivityLogin.class);
                                intent.putExtra("receivedUID",receivedUserID);
                                startActivity(intent);
                                finish();
                            }


                        }
                        if(pendingDynamicLinkData == null){
                            Intent intent = new Intent(SplashScreen.this,ActivityLogin.class);
                            startActivity(intent);
                            receivedUserID = null;
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Login Dynamic Link", "Unable to get Link: "+ e.getMessage());
                        //Toast.makeText(SplashScreen.this, "Link not Found", Toast.LENGTH_SHORT).show();
                        receivedUserID = null;
                        Intent intent = new Intent(SplashScreen.this,ActivityLogin.class);
                        startActivity(intent);
                        finish();
                    }
                });
    }

    private class Sync extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            getPendingLinks();
            return null;
        }
    }
}
