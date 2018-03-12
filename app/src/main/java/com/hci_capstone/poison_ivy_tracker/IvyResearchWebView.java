package com.hci_capstone.poison_ivy_tracker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * Created by Douglas on 3/12/2018.
 */

public class IvyResearchWebView  extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ivy_research_web_view);



        webView = findViewById(R.id.Ivy_Research_Web_View);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                ProgressBar pb = findViewById(R.id.progressBar);
                pb.setVisibility(View.INVISIBLE);
            }
        });
        webView.loadUrl("https://vtnews.vt.edu/articles/2016/07/070516-cals-poisinuvy.html");

    }


}
