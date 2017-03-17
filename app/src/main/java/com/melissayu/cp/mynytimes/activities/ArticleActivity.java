package com.melissayu.cp.mynytimes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.melissayu.cp.mynytimes.Article;
import com.melissayu.cp.mynytimes.R;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Article article = (Article) getIntent().getParcelableExtra("article");

        WebView wv = (WebView) findViewById(R.id.wvArticle);

        wv.setWebViewClient(new WebViewClient(){
            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, request);
                view.loadUrl(url);
                return true;
            }
        });
        wv.loadUrl(article.getWebUrl());
    }

}
