package com.netease.newsprac;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.netease.newsprac.WorkingClass.RSSElements;

import org.w3c.dom.Text;

import java.net.URL;

public class NewsDetailsActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private ProgressBar progressBar;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        Intent intent = getIntent();

        String actionBarTitle = "Web Page";
        if (intent != null && intent.hasExtra(RSSElements.TITLE)) {
            actionBarTitle = intent.getStringExtra(RSSElements.TITLE);
        }
        this.setTitle(actionBarTitle);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        webView = (WebView) findViewById(R.id.news_detail_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClients());
        if (intent != null && intent.hasExtra(RSSElements.LINK)) {
            webView.setInitialScale(1);
            webView.loadUrl(intent.getStringExtra(RSSElements.LINK));
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

    }

    private class MyWebViewClients extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setProgress(100);
            progressBar.setVisibility(View.GONE);
            super.onPageFinished(view, url);
        }
    }

}
