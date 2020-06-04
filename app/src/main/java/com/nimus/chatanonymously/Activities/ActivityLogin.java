package com.nimus.chatanonymously.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteApi;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInResult;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.nimus.chatanonymously.R;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActivityLogin extends AppCompatActivity {

    private CardView login;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private final String TAG = "Activity login TAG";
    private static final int REQ_ONE_TAP = 1;
    private ProgressBar progressBar;
    private CircleImageView logo;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;
    private ImageView info;
    private TextView infoText;
    private boolean isVisible = false;
    private String receivedUserID = null;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        finishAndRemoveTask();
        System.exit(0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);



        mAuth = FirebaseAuth.getInstance();

        receivedUserID = getIntent().getStringExtra("receivedUID");



        oneTapClient = Identity.getSignInClient(this);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        login = findViewById(R.id.signInBtn);
        progressBar = findViewById(R.id.progress_login);
        logo = findViewById(R.id.logo_login);
        info = findViewById(R.id.infoLogin);
        infoText = findViewById(R.id.LogininfoText);


        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isVisible){
                    infoText.setVisibility(View.VISIBLE);
                    isVisible = true;
                }
                else if(isVisible){
                    infoText.setVisibility(View.GONE);
                    isVisible = false;
                }
            }
        });



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LaunchOneTap();
                Toast.makeText(ActivityLogin.this, "Loading...", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_ONE_TAP) {
            try {
                SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(data);
                String idToken = credential.getGoogleIdToken();
                String username = credential.getId();
                String password = credential.getPassword();
                if (idToken != null) {
                    // Got an ID token from Google. Use it to authenticate
                    // with your backend.
                    Log.d("amnajkhdkjahdkahdka", "Got ID token.");
                    firebaseAuthWithGoogle(idToken);
                } else if (password != null) {
                    // Got a saved username and password. Use them to authenticate
                    // with your backend.
                    Log.d("amnajkhdkjahdkahdka", "Got password.");
                }
                else{
                    Log.d("amnajkhdkjahdkahdka", "Got Nothing");
                }
            } catch (ApiException e) {
                // ...
            }
        }
    }


    private void firebaseAuthWithGoogle(String idToken) {

        logo.setImageDrawable(getResources().getDrawable(R.drawable.bg_ncn,getTheme()));
        progressBar.setVisibility(View.VISIBLE);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(mAuth.getCurrentUser().getUid());
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id",mAuth.getCurrentUser().getUid());
                            hashMap.put("name",mAuth.getCurrentUser().getDisplayName());
                            hashMap.put("email",mAuth.getCurrentUser().getEmail());
                            hashMap.put("image",mAuth.getCurrentUser().getPhotoUrl().toString());

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){

                                        if(receivedUserID == null){
                                            Intent intent = new Intent(ActivityLogin.this,MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else{
                                            Intent intent = new Intent(ActivityLogin.this,ActivitySearchUser.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("receivedUID",receivedUserID);
                                            startActivity(intent);
                                            finish();
                                        }

                                    }
                                    else{
                                        Toast.makeText(ActivityLogin.this, ""+task.getException()   , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(ActivityLogin.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            Toast.makeText(ActivityLogin.this, "failed", Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            logo.setImageDrawable(getResources().getDrawable(R.drawable.pet,getTheme()));
                        }

                    }
                });
    }
    // [END auth_with_google]

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){
            Intent intent = new Intent(ActivityLogin.this,MainActivity.class);
            startActivity(intent);
            Log.d("amnajkhdkjahdkahdka","Name: "+ currentUser.getDisplayName() + "Email: "+ currentUser.getEmail());
        }
        else{
            Log.d("amnajkhdkjahdkahdka","Empty User");
        }
    }

    void LaunchOneTap(){
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(ActivityLogin.this, new OnSuccessListener<BeginSignInResult>() {
                    @Override
                    public void onSuccess(BeginSignInResult result) {
                        try {
                            startIntentSenderForResult(
                                    result.getPendingIntent().getIntentSender(), REQ_ONE_TAP,
                                    null, 0, 0, 0);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, "Couldn't start One Tap UI: " + e.getLocalizedMessage());
                        }
                    }
                })
                .addOnFailureListener(ActivityLogin.this,new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //No saved credentials found. Launch the One Tap sign-up flow, or
                        // do nothing and continue presenting the signed-out UI.
                        Toast.makeText(ActivityLogin.this, "Yes: "+e, Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
