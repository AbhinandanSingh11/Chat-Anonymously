package com.nimus.chatanonymously.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nimus.chatanonymously.R;

public class Browser extends AppCompatActivity {
    private WebView browser;
    private String URL;
    private WebSettings webSettings;
    private ProgressBar progressBar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Browser.this,ProfileActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        Toast.makeText(this, "Loading...", Toast.LENGTH_SHORT).show();

        browser = findViewById(R.id.browser);
        progressBar = findViewById(R.id.progressBrowser);


        URL = getIntent().getStringExtra("URL");

        browser.setWebChromeClient(new myWebChromeClient());
        browser.setWebViewClient(new WebViewClient());
        webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl(URL);
    }

    public class myWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }

        public void setValue(int progress){

            if(progress == 100){
                progressBar.setVisibility(View.GONE);
            }
            else{
                progressBar.setVisibility(View.VISIBLE);
            }

            progressBar.setProgress(progress);
        }
    }
}
