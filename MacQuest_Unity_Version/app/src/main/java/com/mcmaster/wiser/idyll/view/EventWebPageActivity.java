package com.mcmaster.wiser.idyll.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mcmaster.wiser.idyll.R;

//

public class EventWebPageActivity extends AppCompatActivity {

    WebView wv_produce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_web_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent intent = getIntent();
        String URL = intent.getStringExtra("URL");
        String title = intent.getStringExtra("Name");

        if(title != null){
            setTitle(title);
        }



        wv_produce = (WebView) findViewById(R.id.wv_event_page);

        //Project the Javascript injection from web page.
        class JsObject {
            @JavascriptInterface
            public String toString() { return "injectedObject"; }
        }
        wv_produce.addJavascriptInterface(new JsObject(), "injectedObject");


        //Load page.
        wv_produce.loadUrl(URL);

        //For security reason, disable the javascript first.
        //wv_produce.getSettings().setJavaScriptEnabled(true);

        wv_produce.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });




    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wv_produce.clearCache(true);

    }


    @Override
    public void onBackPressed() {
        if(wv_produce.canGoBack()){
            wv_produce.goBack();
        }else{
            super.onBackPressed();
        }
    }
}
