package com.nimus.chatanonymously.Login;

import android.content.Context;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.nimus.chatanonymously.R;

public class OneTapBuilder {
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private Context context;

    public OneTapBuilder(Context context){
        this.context = context;
    }





    public void Builder(){
        oneTapClient = Identity.getSignInClient(context);
        signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build())
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        .setServerClientId(context.getResources().getString(R.string.default_web_client_id))
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();
    }

    public SignInClient getOneTapClient() {
        return oneTapClient;
    }

    public BeginSignInRequest getSignInRequest() {
        return signInRequest;
    }

    public Context getContext() {
        return context;
    }
}
