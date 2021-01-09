package com.example.client.ui.news;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;

public class NewsActivity extends AppCompatActivity {
    private Bundle extras;
    private Intent intent;
    private WebView webView;
    private TextView titleText;
    private TextView timeText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_news);
        webView=(WebView)findViewById(R.id.news_webview);
        titleText=(TextView)findViewById(R.id.news_content_title);
        timeText=(TextView)findViewById(R.id.news_content_time);
        intent=getIntent();
        extras= intent.getExtras();
        int position=(int)extras.get("position");
        String url=extras.getString("url");
        String title=extras.getString("title");
        String time=extras.getString("time");
        titleText.setText(title);
        timeText.setText(time);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBlockNetworkLoads(false);
    }
}
