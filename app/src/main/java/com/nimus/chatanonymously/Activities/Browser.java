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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nimus.chatanonymously.R;

public class Browser extends AppCompatActivity {
    private WebView browser;
    private String URL;
    private WebSettings webSettings;
    private FloatingActionButton closeBrowser;

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
        closeBrowser = findViewById(R.id.closeBrowser);

        closeBrowser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Browser.this,ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });


        URL = getIntent().getStringExtra("URL");

        browser.setWebChromeClient(new WebChromeClient());
        browser.setWebViewClient(new WebViewClient());
        webSettings = browser.getSettings();
        webSettings.setJavaScriptEnabled(true);
        browser.loadUrl(URL);
    }
}
