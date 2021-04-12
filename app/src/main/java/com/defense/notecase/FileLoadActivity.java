package com.defense.notecase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class FileLoadActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private String url,fileType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_load);

        progressBar = findViewById(R.id.progressBar);
        webView = findViewById(R.id.webView);
        url = getIntent().getStringExtra("url");
        fileType = getIntent().getStringExtra("fileType");
        progressBar.setVisibility(View.VISIBLE);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebChromeClient(new WebChromeClient());



        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url) {
                webView.loadUrl("javascript:(function() { " +
                        "document.querySelector('[role=\"toolbar\"]').remove();})()");
                progressBar.setVisibility(View.GONE);
            }
        });

        if(fileType.contains("image"))
        {
            url = url;
            webView.loadUrl(url);
        }
        else {
            try {
                url= URLEncoder.encode(url,"UTF-8");
                webView.loadUrl("https://docs.google.com/gview?embedded=true&url="+url);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


    }
}