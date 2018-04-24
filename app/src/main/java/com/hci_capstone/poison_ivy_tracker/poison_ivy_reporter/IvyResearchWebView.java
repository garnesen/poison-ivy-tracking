package com.hci_capstone.poison_ivy_tracker.poison_ivy_reporter;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.hci_capstone.poison_ivy_tracker.R;

/**
 * A webview for loading external web pages.
 */
public class IvyResearchWebView  extends Activity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ivy_research_web_view);

        webView = findViewById(R.id.Ivy_Research_Web_View);
        WebSettings webSettings = webView.getSettings();
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
